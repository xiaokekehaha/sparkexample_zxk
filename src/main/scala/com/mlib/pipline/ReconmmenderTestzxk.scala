package com.mlib.pipline
import scala.collection.Map
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.recommendation._
import org.apache.spark.rdd.RDD
import breeze.linalg.split
class ReconmmenderTestzxk {

}
object ReconmmenderTestzxk extends App {

  // 设置运行环境
  val conf = new SparkConf().setAppName("recommend").setMaster("local[2]")
  val sc = new SparkContext(conf)

  //装载数据集
  //  val rawData = sc.textFile("file:///d:/keamstestdata.txt", 1)
  val rawUserArtistData = sc.textFile("file:///d:/sparktestdata/user_artist_data.txt")
  val rawArtistData = sc.textFile("file:///d:/sparktestdata/artist_data2.txt")
  val rawArtistAlias = sc.textFile("file:///d:/sparktestdata/artist_alias.txt")

  preparation(rawUserArtistData, rawArtistData, rawArtistAlias)

  model(sc, rawUserArtistData, rawArtistData, rawArtistAlias)

  evaluate(sc, rawUserArtistData, rawArtistAlias)

  def evaluate(sc: SparkContext,
    rawUserArtistData: RDD[String],
    rawArtistAlias: RDD[String]) = {

    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))
    val allData = buildRatings(rawUserArtistData, bArtistAlias)

    val Array(trainData, cvData) = allData.randomSplit(Array(0.9, 0.1))
    trainData.cache()
    cvData.cache()

    val allItemIDs = allData.map(_.product).distinct.collect()

    val bAllItemIDs = sc.broadcast(allItemIDs)

    //predictMostListened(sc, trainData)

    val mostListenedAUC = areaUnderCurve(cvData, bAllItemIDs, predictMostListened(sc, trainData))

  }
  def areaUnderCurve(
    positiveData: RDD[Rating],
    bAllItemIDs: Broadcast[Array[Int]],
    predictFunction: (RDD[(Int, Int)] => RDD[Rating])) = {

    val positiveUserProducts = positiveData.map(r => (r.user, r.product))
    val positivePredictions = predictFunction(positiveUserProducts).groupBy(_.user)

   val negativeUserProducts = positiveUserProducts.groupByKey().mapPartitions {
      val random = new Random()
      val allItemIDs = bAllItemIDs.value
      userIDAndPosItemIDs =>

        userIDAndPosItemIDs.map {

          case (userID, posItemIDs) =>

            val posItemIDSet = posItemIDs.toSet

            val negative = new ArrayBuffer[Int]()
            var i = 0

            while (i < allItemIDs.size && negative.size < posItemIDSet.size) {
              val itemID = allItemIDs(random.nextInt(allItemIDs.size))
              if (!posItemIDSet.contains(itemID)) {
                negative += itemID
              }
              i += 1
            }
            // Result is a collection of (user,negative-item) tuples
         negative.map(itemID => (userID, itemID))
        }
    }.flatMap(t => t)
    
    

  }

  def predictMostListened(sc: SparkContext, train: RDD[Rating])(allData: RDD[(Int, Int)]) = {

    val bListenCount =
      sc.broadcast(train.map(r => (r.product, r.rating)).reduceByKey(_ + _).collectAsMap())
    allData.map {
      case (user, product) =>
        Rating(user, product, bListenCount.value.getOrElse(product, 0.0))
    }
  }

  def model(sc: SparkContext,
    rawUserArtistData: RDD[String],
    rawArtistData: RDD[String],
    rawArtistAlias: RDD[String]): Unit = {

    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))

    val trainData = buildRatings(rawUserArtistData, bArtistAlias).cache()
    val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)

    trainData.unpersist()

    println("=====" + model.userFeatures.mapValues(_.mkString(", ")).first())
    val userID = 1000002
    val recommendations = model.recommendProducts(userID, 5)
    recommendations.foreach(println)

    val recommendedProductIDs = recommendations.map(_.product).toSet

    val data = rawUserArtistData.map(_.split(" "))
    val rawArtistsForUser = rawUserArtistData.map(_.split(' ')).
      filter { case Array(user, _, _) => user.toInt == userID }

    val artistByID = buildArtistByID(rawArtistData)

    val existingProducts = rawArtistsForUser.map { case Array(_, artist, _) => artist.toInt }.
      collect().toSet

    val data2 = artistByID.filter { case (id, name) => existingProducts.contains(id) }.values.collect.foreach(println)
    //rawUserArtistData.map(_.split(" ")).filter{ case x => x(0).toInt == userID }
    unpersist(model)
  }
  def unpersist(model: MatrixFactorizationModel): Unit = {
    // At the moment, it's necessary to manually unpersist the RDDs inside the model
    // when done with it in order to make sure they are promptly uncached
    model.userFeatures.unpersist()
    model.productFeatures.unpersist()
  }
  def buildRatings(
    rawUserArtistData: RDD[String],
    bArtistAlias: Broadcast[Map[Int, Int]]) = {

    rawUserArtistData.map {
      line =>
        // val data=line.split(" ").map(_.toInt)
        val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
        val finalArtistID = bArtistAlias.value.getOrElse(artistID, artistID)
        Rating(userID, finalArtistID, count)

    }

  }

  def preparation(rawUserArtistData: RDD[String],
    rawArtistData: RDD[String],
    rawArtistAlias: RDD[String]) = {

    val userIDStats = rawUserArtistData.map(_.split(" ")(0).toDouble).stats()
    val itemIDStats = rawUserArtistData.map(_.split(" ")(1).toDouble).stats()

    val artistByID = buildArtistByID(rawArtistData)
    val artistAlias = buildArtistAlias(rawArtistAlias)

    val (badID, goodID) = artistAlias.head

    val data = artistAlias.head
    print("=====" + data)
    // println(artistByID.lookup(badID) + " -> " + artistByID.lookup(goodID))

  }
  def buildArtistByID(rawArtistData: RDD[String]) = {

    rawArtistData.flatMap {

      line =>
        val (id, name) = line.span(_ != '\t')
        if (name.isEmpty) {
          None
        } else {
          try {
            Some((id.toInt, name.trim))
          } catch {
            case e: NumberFormatException => None
          }
        }

    }

  }
  def buildArtistAlias(rawArtistAlias: RDD[String]): Map[Int, Int] = {
    rawArtistAlias.flatMap { line =>

      val tokens = line.split('\t')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0).toInt, tokens(1).toInt))
      }
    }.collectAsMap()
  }

}