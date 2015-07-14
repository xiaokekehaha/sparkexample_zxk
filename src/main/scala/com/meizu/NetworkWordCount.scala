package com.meizu

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.spark.storage.StorageLevel
import java.util.Properties
import java.io.BufferedInputStream
import java.io.FileInputStream
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.HTablePool
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.PairRDDFunctions

/**
 * zxk
 */
object NetworkWordCount {
  def main(args: Array[String]) {
    /*   if (args.length < 4) {
      System.err.println("Usage: NetworkWordCount <master> <hostname> <port> <seconds>\n" +
        "In local mode, <master> should be 'local[n]' with n > 1")
      System.exit(1)
    }
*/
    //StreamingExamples.setStreamingLogLevels()

    // 新建StreamingContext
    val ssc = new StreamingContext("local[2]", "NetworkWordCount", Seconds("1".toInt))

    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    
    
    
    // val hashTags = lines.flatMap(status => status.split(" ").filter(_.startsWith("#")))
    
       
    val topCounts10 = words.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
      .map { case (topic, count) => (count, topic) }
     
     
   val topCounts11=topCounts10.transform{
     rdd=>
       val line1=rdd.map{
         
         line2=>
           
           (line2._1,1)
       }
       
       line1.reduceByKey(_+_)
     
   }
   
 //  new PairRDDFunctions
    topCounts11.foreachRDD(rdd => {
      //val topList = rdd.take(1)
      println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
      rdd.foreach{
        line=>
         println(line._1+"==="+line._2)    
        
      }
     // topList.foreach { case (count, tag) => println("%s (%s tweets)".format(tag, count)) }
    })


    //wordCounts.foreach(foreachFunc)
    //wordCounts.foreach((e: (String, Int)) => println(e._1 + "=" + e._2))
    //  wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }
  
}

