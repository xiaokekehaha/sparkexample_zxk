package cn.chinahadoop.streaming

import java.util.Properties
 


import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf


object KafkaTest {
  
  def main(args:Array[String])
  {
    if (args.length < 5) {
      System.err.println("Usage: KafkaTest <zkQuorum> <group> <topics> <numThreads> <output>")
      System.exit(1)
    }
    val Array(zkQuorum, group, topics, numThreads,output) = args
    val sparkConf = new SparkConf().setAppName("KafkaTest")
    val ssc =  new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")
    
    val topicpMap = topics.split(",").map((_,numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicpMap).map(_._2) 
    lines.saveAsTextFiles(output)
  
 val  kafkaParams =Map("metadata.broker.list"-> "www.iteblog.com:9092,anotherhost:9092")  
 val topics2 =Set("sometopic", "iteblog")
  val kafkaStream =KafkaUtils.createDirectStream(ssc, kafkaParams, topics2) 
    ssc.start()
    ssc.awaitTermination()
    
    //.saveAsTextFile(output)
    
  }
  }
