package cn.chinahadoop.streaming

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Milliseconds

object FlumeEventPrint3 {
  def main(args: Array[String]) {
    /*
    if (args.length < 2) {
      System.err.println(
        "Usage: FlumeEventCount <host> <port>")
      System.exit(1)
    }
    */


    //val Array(host, port) = args

    val batchInterval = Milliseconds(2000)

    // Create the context and set the batch size
    val sparkConf = new SparkConf().setAppName("FlumeEventCount").setMaster("local")
    val ssc = new StreamingContext(sparkConf, batchInterval)

    // Create a flume stream
    val stream = FlumeUtils.createStream(ssc, "192.168.7.127", 33333)
    //stream.saveAsTextFiles("nothing")
    // Print out the count of events received from this server in each batch
    //stream.map{cnt => "Received " + cnt mkString " " + " flume events." }.saveAsTextFiles("nothing/")
    stream.map(e=>e.event).print()

    ssc.start()
    ssc.awaitTermination()
  }
}