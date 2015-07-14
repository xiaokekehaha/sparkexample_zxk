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
class AgeModelTest3 {

}

object AgeModelTest3 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("age").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val music = sc.textFile("file:///d:/sparktestdata/dim_music/*")

    val ret_music = sc.textFile("file:///d:/sparktestdata/ret_music_test.txt")
    // val music_singer = sc.textFile("file:///d:/sparktestdata/dim_music_singer/")

    val data_music = data_deal_music(music)
    val music_data_with_index = data_music.zipWithIndex
    val result = data_deal_ret_music(sc, music_data_with_index, ret_music)

    
     result.foreach(println)
    val data3 = music_data_with_index.take(10)
    data3.foreach(println)
    

    //  data_deal_music_siger(music_singer)

  }

  def data_deal_ret_music(sc: SparkContext, music_data_with_index: RDD[(String, Long)], ret_music: RDD[String]) :RDD[String]= {

    val bArtistAlias = sc.broadcast(buildArtistAlias(music_data_with_index).collectAsMap)
    //val bArtistAlias=buildArtistAlias(music_data_with_index).collectAsMap
    val data = ret_music.map(_.split(",")(1)).map(_.split(" "))
    val datacopy = ret_music.map(_.split(",")(0))
    //val data2=bArtistAlias.value.size

    data.map{
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

    
    }
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




