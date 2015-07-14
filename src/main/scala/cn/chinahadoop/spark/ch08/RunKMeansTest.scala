package cn.chinahadoop.spark.ch08
import org.apache.spark.mllib.clustering._
import org.apache.spark.mllib.linalg._
import org.apache.spark.rdd._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors

class RunKMeansTest {

}

object RunKMeansTest {
  
  def main(args: Array[String]) {

    // 设置运行环境
    val conf = new SparkConf().setAppName("Kmeans").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //装载数据集
    val rawData = sc.textFile("file:///d:/sparktestdata/data.log", 1)
    //clusteringTake0(rawData)
  
   // clusteringTake0(rawData)
  // clusteringTake1(rawData)
   clusteringTake2(rawData)
  //  clusteringTake3(rawData)
   // clusteringTake4(rawData)

    sc.stop()
  }
  // Clustering, Take 3

  def buildCategoricalAndLabelFunction(rawData: RDD[String]): (String => (String,Vector)) = {
    val splitData = rawData.map(_.split(','))
    val protocols = splitData.map(_(1)).distinct().collect().zipWithIndex.toMap
    val services = splitData.map(_(2)).distinct().collect().zipWithIndex.toMap
    val tcpStates = splitData.map(_(3)).distinct().collect().zipWithIndex.toMap
    (line: String) => {
      val buffer = line.split(',').toBuffer
      val protocol = buffer.remove(1)
      val service = buffer.remove(1)
      val tcpState = buffer.remove(1)
      val label = buffer.remove(buffer.length - 1)
      val vector = buffer.map(_.toDouble)

      val newProtocolFeatures = new Array[Double](protocols.size)
      newProtocolFeatures(protocols(protocol)) = 1.0
      val newServiceFeatures = new Array[Double](services.size)
      newServiceFeatures(services(service)) = 1.0
      val newTcpStateFeatures = new Array[Double](tcpStates.size)
      newTcpStateFeatures(tcpStates(tcpState)) = 1.0

      vector.insertAll(1, newTcpStateFeatures)
      vector.insertAll(1, newServiceFeatures)
      vector.insertAll(1, newProtocolFeatures)

      (label, Vectors.dense(vector.toArray))
    }
  }

  
  def clusteringTake3(rawData: RDD[String]): Unit = {
    val parseFunction = buildCategoricalAndLabelFunction(rawData)
    
    // parseFunction(rawData)
    val data = rawData.map(parseFunction).values
    
     val data2=data.map(_.toArray)
    
    data2.foreach(a => {
    a.foreach(e => print(e + " "))
    println()
  })

    
    val normalizedData = data.map(buildNormalizationFunction(data)).cache()

    (80 to 160 by 10).map(k =>
      (k, clusteringScore2(normalizedData, k))).toList.foreach(println)

    normalizedData.unpersist()
  }
   // Clustering, Take 1

  def distance(a: Vector, b: Vector) =
    math.sqrt(a.toArray.zip(b.toArray).map(p => p._1 - p._2).map(d => d * d).sum)

  def distToCentroid(datum: Vector, model: KMeansModel) = {
    val cluster = model.predict(datum)
    val centroid = model.clusterCenters(cluster)
    distance(centroid, datum)
  }

  def clusteringScore(data: RDD[Vector], k: Int): Double = {
    val kmeans = new KMeans()
    kmeans.setK(k)
    val model = kmeans.run(data)
    data.map(datum => distToCentroid(datum, model)).mean()
  }

  def clusteringScore2(data: RDD[Vector], k: Int): Double = {
    val kmeans = new KMeans()
    kmeans.setK(k)
   // kmeans.setRuns(10)
   // kmeans.setEpsilon(1.0e-6)
    val model = kmeans.run(data)
    data.map(datum => distToCentroid(datum, model)).mean()
  }
  
  
  def clusteringTake1(rawData: RDD[String]): Unit = {

    val data = rawData.map { line =>
      val buffer = line.split(',').toBuffer
      buffer.remove(1, 3)
      buffer.remove(buffer.length - 1)
      Vectors.dense(buffer.map(_.toDouble).toArray)
    }.cache()

/*      (line: String) => {
        
        
        
      }*/
    (5 to 30 by 5).map(k => (k, clusteringScore(data, k))).
      foreach(println)

    (30 to 100 by 10).par.map(k => (k, clusteringScore2(data, k))).
      toList.foreach(println)

    data.unpersist()

  }

  def clusteringTake2(rawData:RDD[String])={
     
    val data=  rawData.map{
       line=>
       val  buffer=line.split(",").toBuffer
       buffer.remove(1,3)
       buffer.remove(buffer.length-1)
       Vectors.dense(buffer.map(_.toDouble).toArray)
     }
    
    
     val dataAsArray1 = data.map(_.toArray)
     val numCols1 = dataAsArray1.first()
     numCols1.foreach{x=>print(x+",")}
     
     
     
    val normalizedData = data.map(buildNormalizationFunction(data)).cache() 
   
     val dataAsArray = normalizedData.map(_.toArray)
     val numCols = dataAsArray.first()
     numCols.foreach{x=> print(x+",")}
  
    /*(5 to 30 by 5).map(k =>
    (k, clusteringScore2(normalizedData, k))).
      foreach(println)*/
  /*  (60 to 120 by 10).par.map(k =>
      (k, clusteringScore2(normalizedData, k))).toList.foreach(println)*/

    normalizedData.unpersist()
    
  }

  def buildNormalizationFunction(data: RDD[Vector]) = {
    
    val dataAsArray = data.map(_.toArray)
    val numCols = dataAsArray.first().length
    val n = dataAsArray.count()
    val sums = dataAsArray.reduce(
      (a, b) => a.zip(b).map(t => t._1 + t._2))
      
      
    val sumSquares = dataAsArray.fold(
        new Array[Double](numCols)
      )(
        (a, b) => a.zip(b).map(t => t._1 + t._2 * t._2)
      )
    val stdevs = sumSquares.zip(sums).map {
      case (sumSq, sum) => math.sqrt(n * sumSq - sum * sum) / n
    }
    val means = sums.map(_ / n)

    (datum: Vector) => {
      val normalizedArray = (datum.toArray, means, stdevs).zipped.map(
        (value, mean, stdev) =>
          if (stdev <= 0)  (value - mean) else  (value - mean) / stdev
      )
      Vectors.dense(normalizedArray)
    }
    
  }
  
   def clusteringTake0(rawData: RDD[String]): Unit = {

    rawData.map(_.split(',').last).countByValue().toSeq.sortBy(_._2).reverse.foreach(println)
    
   // val data= rawData.map(_.split(',').last).countByValue().toSeq.sortBy(_._2).reverse.foreach(println)

   val labelsAndData = rawData.map { line =>{
      val buffer = line.split(',').toBuffer
      buffer.remove(1, 3)
      val label = buffer.remove(buffer.length - 1)
      val vector = Vectors.dense(buffer.map(_.toDouble).toArray)
      (label, vector)
    }
   }

   val data = labelsAndData.values.cache()
/*   val data1=data.map(_.toArray)

   
     val numCol=   data1.first()
     
     for(x<-numCol){
       
       println(x+"===")
     }*/
  // val sums = data1.map(x=>x)
  // sums.foreach(println)
   

    val kmeans = new KMeans()
    val model = kmeans.run(data)
    val data2= model.clusterCenters.length
    println("==="+data2)
 
    model.clusterCenters.foreach(println)

    val clusterLabelCount = labelsAndData.map { case (label, datum) =>
      val cluster = model.predict(datum)
      (cluster, label)
    }.countByValue()

    clusterLabelCount.toSeq.sorted.foreach { case ((cluster, label), count) =>
      println(f"$cluster%1s$label%18s$count%8s")
    }

    data.unpersist()
  }

}