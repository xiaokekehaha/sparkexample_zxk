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
import org.apache.spark.rdd.RDD
import scala.collection.mutable.ArrayBuffer
import scala.collection.Map

import scala.util.Random

import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.recommendation._
import org.apache.spark.rdd.RDD
class cha05classicTest {

}

object cha05classicTest{
  
  def main(args: Array[String]) {
  
    val conf = new SparkConf().setAppName("age")
    val hadoopConf = new Configuration()
    val sc = new SparkContext(conf)

    val rawdata=sc.textFile("")
    
    val records=rawdata.map(_.split("\t"))
    
    import org.apache.spark.mllib.regression.LabeledPoint
    import org.apache.spark.mllib.linalg.Vectors
    val data = records.map{
      
    line =>
     val trimmed=line.map(_.replaceAll("\"", "")) 
     val label=trimmed(trimmed.size-1).toInt
     val feature=trimmed.slice(4, line.size-1).map(d=> if(d=="?") 0.0 else d.toDouble )

    LabeledPoint(label, Vectors.dense(feature))
    }
    

    val numdata=records.map{
      
    line =>
     val trimmed=line.map(_.replaceAll("\"", "")) 
     val label=trimmed(trimmed.size-1).toInt
     val feature=trimmed.slice(4, line.size-1).map(d=> if(d=="?") 0.0 else d.toDouble )

    LabeledPoint(label, Vectors.dense(feature))
    }
    
    
    
    
  
  
  
  }
  
}