package com.mlib.pipline
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{ Path, FileSystem }
import org.apache.spark.mllib.feature.{ StandardScalerModel, StandardScaler }
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{ SparkContext, SparkConf }

import org.apache.spark.SparkContext._
import sun.security.util.Length
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
class AgeModle {

}
object AgeModle extends App{
    val conf = new SparkConf().setAppName("Home Price Recommender").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val app1 = sc.textFile("file:///d:/sparktestdata/app1.txt").filter(_.split("|").size>3)
    val app2= sc.textFile("file:///d:/sparktestdata/app2.txt").filter(_.split("|").size>3)
    val app3= sc.textFile("file:///d:/sparktestdata/app3.txt").filter(_.split("|").size>3)
    
    /*val s = app1.map(line => line.split("\t")).cache()
    val r1 = s.map(x => (x(0) + ":" + x(2), 1)).reduceByKey(_ + _).sortBy(x => x._1.split(":")(0)).sortBy(x => x._2)
    r1.collect.foreach(println)*/
   // val rdd1 = sc.parallelize(Seq((1, "A"), (2, "B"), (3, "C")))
   /* 
   val data1= app1.flatMap{line =>
       val tokens = line.split('|')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0), tokens(1)+":"+tokens(2)))
      }
     }.reduceByKey((x,y)=>x+","+y)
    
    val data2= app2.flatMap{line =>
       val tokens = line.split('|')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0), tokens(1)+":"+tokens(2)))
      }
     }.reduceByKey((x,y)=>x+","+y)
     */
      
   val data1= app1.flatMap{line =>
       val tokens = line.split('|')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0), tokens(1)+":"+tokens(2)))
      }
     }
    
    val data2= app2.flatMap{line =>
       val tokens = line.split('|')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0), tokens(1)+":"+tokens(2)))
      }
     }
      val data3= app3.flatMap{line =>
       val tokens = line.split('|')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0), tokens(1)+":"+tokens(2)))
      }
     }
     //rdd1.join(rdd2).map(case (k, (ls, rs)) => (k, ls ++ rs))
      val uniondata= data1.union(data2).union(data3).reduceByKey((x,y)=>x+","+y)
      
      uniondata.foreach(println)   
      
     val labelData2 = uniondata.map { data =>

      val data1 = data._1.split(",")
      LabeledPoint(data1(0).toDouble, Vectors.dense(data._2.trim().split(',').map(java.lang.Double.parseDouble)))
    }.cache() 
      
    
   /*  val joindata=data1.join(data2)
     joindata.foreach(println)*/
  
     
    
    
    
}