package com.meizu
import org.apache.hadoop.conf.Configuration

import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.SparkContext._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd.RDD
class Test {

}

object Test {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("age")
    //false

    System.setProperty("spark.hadoop.mapred.output.compress", "false")
    System.setProperty("spark.hadoop.mapred.output.compression.codec", "false")
    //System.setProperty("spark.hadoop.mapred.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec")
    System.setProperty("spark.hadoop.mapred.output.compression.type", "BLOCK")
    val sc = new SparkContext(conf)

    sc.hadoopConfiguration.set("mapred.output.compress", "false")
    
    
    val music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/data/age_model/music/20150429/dim_music/*").cache()
    val ret_music = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/ret_music_test.txt").cache()

    val result3 = ret_music.map(_.split(',')(1)).map((_, 1))

    result3.saveAsTextFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/age/music/06/")

   // result3.saveAsHadoopFile("hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/age/music/06/", classOf[String], classOf[String], classOf[MultipleTextOutputFormat[String, String]], )
  }

}