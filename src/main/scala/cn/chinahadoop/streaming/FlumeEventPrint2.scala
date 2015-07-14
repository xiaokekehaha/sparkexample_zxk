package cn.chinahadoop.streaming

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Milliseconds

object FlumeEventPrint2 {
  def main(args: Array[String]) {
    /*
    if (args.length < 2) {
      System.err.println(
        "Usage: FlumeEventCount <host> <port>")
      System.exit(1)
    }
    */

    val batchInterval = Milliseconds(2000)

    // Create the context and set the batch size
   //   val sparkConf = new SparkConf().setAppName("FlumeEventCount").setMaster("local")
  
    //val ssc = new StreamingContext(sparkConf, batchInterval)
    val ssc = new StreamingContext("spark://192.168.7.127:7077", "streaming", batchInterval,
      System.getenv("SPARK_HOME"))
    // Create a flume stream
    
    val stream = FlumeUtils.createStream(ssc, "192.168.7.127", 33333)
     stream.count().map(cnt => "Received " + cnt + " flume events." ).saveAsTextFiles("/home/yarn/spark/data/20140729")
     ssc.start()
     ssc.awaitTermination(10000)
  
  }
}