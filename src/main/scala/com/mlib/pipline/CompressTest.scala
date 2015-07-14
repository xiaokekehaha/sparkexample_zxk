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
class CompressTest {

}

object CompressTest {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("age")

    System.setProperty("spark.hadoop.mapred.output.compress", "false")
    System.setProperty("spark.hadoop.mapred.output.compression.codec", "false")
    //System.setProperty("spark.hadoop.mapred.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec")
    System.setProperty("spark.hadoop.mapred.output.compression.type", "BLOCK")
    val sc = new SparkContext(conf)

    val music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/data/age_model/music/20150429/dim_music/*").cache()
    val ret_music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/ret_music_test.txt").cache()

    val result3 = ret_music.map(_.split(',')(1)).map((_, 1))

    result3.saveAsTextFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/age/music/06/")

  }

}