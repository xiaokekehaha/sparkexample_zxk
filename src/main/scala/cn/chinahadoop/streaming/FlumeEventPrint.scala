package cn.chinahadoop.streaming

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Milliseconds

object FlumeEventPrint {
  def main(args: Array[String]) {
    /*
    if (args.length < 2) {
      System.err.println(
        "Usage: FlumeEventCount <host> <port>")
      System.exit(1)
    }
    */

    val batchInterval = Milliseconds(20000)

    // Create the context and set the batch size
   //   val sparkConf = new SparkConf().setAppName("FlumeEventCount").setMaster("local")
  
    //val ssc = new StreamingContext(sparkConf, batchInterval)
    val ssc = new StreamingContext("spark://192.168.7.127:7077", "streaming", batchInterval,
      System.getenv("SPARK_HOME"))
    // Create a flume stream
    
    val stream = FlumeUtils.createStream(ssc, "192.168.7.127", 33333)
    //val stream = FlumeUtils.createStream(ssc, "localhost", 33333,StorageLevel.MEMORY_ONLY_SER)
    //stream.saveAsTextFiles("nothing")
    // Print out the count of events received from this server in each batch
    //stream.map{cnt => "Received " + cnt mkString " " + " flume events." }.saveAsTextFiles("nothing/")
    stream.print
    stream.map(e=>e.event).saveAsTextFiles("/home/yarn/spark/data/20140729")

    ssc.start()
    ssc.awaitTermination()
  
  }
}