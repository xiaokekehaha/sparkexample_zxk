package cn.chinahadoop.spark.ch08
import scala.collection.Map
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import kafka.javaapi.producer.Producer
import scala.util.Random

import scala.collection.mutable.ArrayBuffer
class RunRecommenderTest2 {

}

object RunRecommenderTest2 {

  def main(args: Array[String]) {

    // 设置运行环境
    val conf = new SparkConf().setAppName("Kmeans").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //装载数据集
    //  val rawData = sc.textFile("file:///d:/keamstestdata.txt", 1)
    val rawUserArtistData = sc.textFile("file:///d:/sparktestdata/user_artist_data.txt")
    val rawArtistData = sc.textFile("file:///d:/sparktestdata/artist_data2.txt")
    val rawArtistAlias = sc.textFile("file:///d:/sparktestdata/artist_alias.txt")

    preparation(rawUserArtistData, rawArtistData, rawArtistAlias)
    model(sc, rawUserArtistData, rawArtistData, rawArtistAlias)
    evaluate(sc, rawUserArtistData, rawArtistAlias)
    recommend(sc, rawUserArtistData, rawArtistData, rawArtistAlias)

  }
  
  def recommend(sc:SparkContext,rawUserArtistData: RDD[String], rawArtistData: RDD[String],rawArtistAlias:RDD[String])={
    
    
     val bArtistAlias =sc.broadcast(buildArtistAlias(rawArtistAlias))
     
    val allData = buildRatings(rawUserArtistData, bArtistAlias).cache()
    val model = ALS.trainImplicit(allData, 50, 10, 1.0, 40.0)
    allData.unpersist()
    
    val userID = 2093760
    
    val data=model.recommendProducts(userID, 5)
    
    val data2=data.map(r=>r.product).toSet
  
    val artistByID = buildArtistByID(rawArtistData)
     
    artistByID.filter{
       case (id,name)=>data2.contains(id)
       
     }.values.collect.foreach(println)
  
  }

  def evaluate(sc: SparkContext, rawUserArtistData: RDD[String], rawArtistAlias: RDD[String]) = {

    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))
    val allData = buildRatings(rawUserArtistData, bArtistAlias)

    val Array(trainData, cvData) = allData.randomSplit(Array(0.9, 0.1))
    trainData.cache
    cvData.cache

    val allItemIDs = allData.map(_.product).distinct().collect()
    val bAllItemIDs = sc.broadcast(allItemIDs)

   // val data=
    val mostListenedAUC = areaUnderCurve(cvData, bAllItemIDs, predictMostListened(sc, trainData))
    
     val evaluations =
       for (rank   <- Array(10,  50);
           lambda <- Array(1.0, 0.0001);
           alpha  <- Array(1.0, 40.0))
      yield{
         
          val model = ALS.trainImplicit(trainData, rank, 10, lambda, alpha)
        val auc = areaUnderCurve(cvData, bAllItemIDs, model.predict)
        //unpersist(model)
        ((rank, lambda, alpha), auc)
       }
    
     evaluations.sortBy(_._2).reverse.foreach(println)

  }

  
  
  def areaUnderCurve2(positiveData: RDD[Rating],
    bAllItemIDs: Broadcast[Array[Int]],
    predictFunction: RDD[(Int, Int)]) = {
    
    
  }
  
  def areaUnderCurve(positiveData: RDD[Rating],
    bAllItemIDs: Broadcast[Array[Int]],
    predictFunction: (RDD[(Int, Int)] => RDD[Rating])) = {

    val positiveUserProducts = positiveData.map(r => (r.user, r.product))
    val positivePredictions = predictFunction(positiveUserProducts).groupBy(_.user)

    val negativeUserProducts = positiveUserProducts.groupByKey().mapPartitions {

      userIDAndPosItemIDs =>
        val random = new Random()
        val allItemIDs = bAllItemIDs.value
        userIDAndPosItemIDs.map {
          case (userID, posItemIDs) =>
            val posItemIDSet = posItemIDs.toSet
            val negative = new ArrayBuffer[Int]()
            var i = 0

            while(i<allItemIDs.size&&negative.size < posItemIDSet.size){
              
              val itemID = allItemIDs(random.nextInt(allItemIDs.size))
              if (!posItemIDSet.contains(itemID)) {
                   negative += itemID
                  }
              
               i += 1
            }
            negative.map(itemID => (userID, itemID))

        }

    }.flatMap(t => t)

   val negativePredictions = predictFunction(negativeUserProducts).groupBy(_.user) 
    
   val data2= positivePredictions.join(negativePredictions).values.map{
      case (positiveRatings, negativeRatings) =>{
        
        var correct = 0L
        var total = 0L
        
        for(positive<-positiveRatings;
             negative  <-negativeRatings
        ){
          if (positive.rating > negative.rating) {
            correct += 1
          }
          total += 1
          
        }
      correct.toDouble / total    
        
      }
      
    }.mean
   
   
   
  }

  def predictMostListened(sc: SparkContext, trainData: RDD[Rating])(allData: RDD[(Int, Int)]) = {
    val data = trainData.map(r => (r.product, r.rating)).reduceByKey(_ + _).collectAsMap
    val bListenCount = sc.broadcast(data)

    allData.map {
      case (user, product) =>
        Rating(user, product, bListenCount.value.getOrElse(product, 0.0))
    }

  }

  def model(sc: SparkContext, rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {

    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistData))
    val trainData = buildRatings(rawUserArtistData, bArtistAlias)
    val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)

    val data = model.userFeatures.mapValues(_.mkString(",")).first
    val userID = 1000002

    val recommendations = model.recommendProducts(userID, 5)

    val array = Array("4", "e")

    array.toSet

    val data2 = recommendations.map {

      line =>
        line.product
    }

    val rawArtistsForUser = rawUserArtistData.map(_.split(" ")).filter {
      case Array(user, _, _) => user.toInt == userID

    }
    val existingProducts = rawArtistsForUser.map {
      case Array(_, artis, _) => artis.toInt
    }.collect.toSet

    val artistByID = buildArtistByID(rawArtistData)

    val data22 = artistByID.filter {
      case (id, name) => existingProducts.contains(id)
    }

    data22.values.collect.foreach(println)

  }
  def buildRatings(rawUserArtistData: RDD[String], bArtistAlias: Broadcast[Map[Int, Int]]) = {

    rawUserArtistData.map {

      line =>
        val Array(userID, artistID, count) = line.split("\t").map(_.toInt)
        val finalArtistID = bArtistAlias.value.getOrElse(artistID, artistID)
        Rating(userID, finalArtistID, count)

    }

  }

  def preparation(rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {

    val userid = rawUserArtistData.map(_.split(" ")(0).toDouble).stats

    val artistByID = buildArtistByID(rawArtistData)

    val artistAlias = buildArtistAlias(rawArtistAlias)

  }

  def buildArtistAlias(rawArtistAlias: RDD[String]): Map[Int, Int] = {

    rawArtistAlias.flatMap {
      line =>
        val data = line.split("\t")
        if (data(0).isEmpty()) {

          None
        } else {

          Some((data(0).toInt, data(1).toInt))
        }

    }.collectAsMap()

  }

  def buildArtistByID(rawArtistData: RDD[String]) =
    rawArtistData.flatMap { line =>
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