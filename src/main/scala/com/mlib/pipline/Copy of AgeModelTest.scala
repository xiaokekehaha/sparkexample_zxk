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


class AgeModelTest4 {

}

object AgeModelTest4 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("age").setMaster("local[2]")
    val sc = new SparkContext(conf)
    
 val music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/data/age_model/music/20150429/dim_music/*").cache()
 val ret_music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/data/age_model/music/20150429/ret_music/*").cache()

   // val music = sc.textFile("file:///d:/sparktestdata/dim_music/*")
  //  val ret_music = sc.textFile("file:///d:/sparktestdata/ret_music_test.txt") 
    val data_music = data_deal_music(music).cache()
    val music_data_with_index = data_music.zipWithIndex.cache
    
    val bArtistAlias = sc.broadcast(buildArtistAlias(music_data_with_index).collectAsMap)

    //val result = data_deal_ret_music(sc, music_data_with_index, ret_music,bArtistAlias)
    
    
    
    val result2=ret_music.map {
   
     // val data_music_data5 = new ArrayBuffer[String]()
      
      line =>
        
        val data_music_data0 = line.split(",")(0)
        val data_music_data1 = line.split(",")(1)

        val data_music_data2 = data_music_data1.split(" ")
       
        val data=   data_music_data2.map {

          line2 =>

            var data_music_data3 = line2.split(":")(0)
            val data_music_data4 = line2.split(":")(1)

            val data_music_data6 = bArtistAlias.value
          
            if(data_music_data6.contains(data_music_data3)){
              
              data_music_data3 =  data_music_data6(data_music_data3).toString
              
            }
          

         (data_music_data3+":"+data_music_data4)   
        }

      data_music_data0+","+data.mkString(" ")
    }

    
    result2.saveAsTextFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/age/music/03/")
   
  
    result2.take(1).foreach(println)
   // result2.foreach(println)
    val data3 = music_data_with_index.take(10)
    data3.foreach(println)

    //  data_deal_music_siger(music_singer)

  }

  
  def buildArtistAlias(rawArtistAlias: RDD[(String, Long)]) =
    rawArtistAlias.map {
      case (x, y) =>

        val tokens = x.split(',')

        (tokens(0), y)

    }

  def data_deal_music(music: RDD[String]) = {

    music.map {
      line =>

        val data_music = line.split(",")

        ("31" + data_music(0) + "," + data_music(1) + "," + data_music(2))
    }

  }
}


/*data.map{
      line=>
        
        
      val data2 = line.map(_.split(":")(0))
      val data2_value = line.map(_.split(":")(1))
      val data4 = new ArrayBuffer[String]()

      val negative = new ArrayBuffer[Int]()
      for (i <- 1 to (data2.length - 1)) {

        val data3 = bArtistAlias.value.toSeq.sortBy(_._2)
        for ((k, v) <- data3) {
          if (data2(i).equals(k)) {

            data4 += ("31" + v + ":" + data2_value(i))

          }

        }

      }

      val data5 = data4.mkString(" ")

   //   println(data5)
      data5

    
    }*/
    /*// print("=="+data2)
    for (ret_music_data <- data) {

      val data2 = ret_music_data.map(_.split(":")(0))
      val data2_value = ret_music_data.map(_.split(":")(1))

      val data4 = new ArrayBuffer[String]()

      val negative = new ArrayBuffer[Int]()
      for (i <- 1 to (data2.length - 1)) {

        val data3 = bArtistAlias.value.toSeq.sortBy(_._2)
        for ((k, v) <- data3) {
          if (data2(i).equals(k)) {

            data4 += ("31" + v + ":" + data2_value(i))

          }

        }

      }

      val data5 = data4.mkString(" ")

      println(datacopy + "" + data5)

    }
*/



