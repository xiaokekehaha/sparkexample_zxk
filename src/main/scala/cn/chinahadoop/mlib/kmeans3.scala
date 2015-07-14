/*package cn.chinahadoop.mlib
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

*//**
 * Created by askyer on 4/24/14.
 *//*
object kmeans3 {
  def main (args: Array[String]) {
    val sc = new SparkContext("local[2]","UseKMeans","/home/AppSource/spark-0.9")

    val data = sc.textFile("/data/w12data.csv")
    val parsedData = data.map(_.split(",")).filter(_(1).toDouble <0.3).filter(_(2).toDouble > 0.6).map{
      x => Array(x(1).toDouble, x(2).toDouble)
    }

       // val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble)))
    val iters = 100
    val k=5
    val runs = 2
    
   //     val model = KMeans.train(parsedData, numClusters, numIterations)
    val model = KMeans.train(parsedData, k, iters, runs)
    val cost = model.computeCost(parsedData)
    println("Cluster centers:")
    for (c <- model.clusterCenters) {
      println("  " + c.mkString(" "))
    }
    println("Cost: " + cost)
    val valuesAndPreds = data.map {
      line =>
        val parts = line.split(",")
        val prediction = model.predict(Array(parts(1).toDouble, parts(2).toDouble))
        line +"," +prediction
    }.saveAsTextFile("/data/w12result5")

  }
}*/