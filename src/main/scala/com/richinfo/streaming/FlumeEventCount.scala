package com.richinfo.streaming

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.flume._
import org.apache.spark.streaming.dstream.DStream

/**
 *  Produces a count of events received from Flume.
 *
 *  This should be used in conjunction with an AvroSink in Flume. It will start
 *  an Avro server on at the request host:port address and listen for requests.
 *  Your Flume AvroSink should be pointed to this address.
 *
 *  Usage: FlumeEventCount <host> <port>
 *    <host> is the host the Flume receiver will be started on - a receiver
 *           creates a server and listens for flume events.
 *    <port> is the port the Flume receiver will listen on.
 *
 *  To run this example:
 *    `$ bin/run-example org.apache.spark.examples.streaming.FlumeEventCount <host> <port> `
 */
object FlumeEventCount {
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Please input <hostname> <port>")
      System.exit(1)
    }
    if (args.length % 2 != 0 ){
      System.err.println("The <hostname> <port> must be pair")
      System.exit(1)
    }


//    val Array(host, IntParam(port)) = args

    val batchInterval = Milliseconds(2000)

    // Create the context and set the batch size
    val sparkConf = new SparkConf().setAppName("FlumeEventCount")
    val ssc = new StreamingContext(sparkConf, batchInterval)

    // Create a flume stream
//    val stream = FlumeUtils.createStream(ssc, host, port, StorageLevel.MEMORY_ONLY_SER_2)

    val stream1 = FlumeUtils.createStream(ssc, args(0), args(1).toInt, StorageLevel.MEMORY_ONLY_SER_2)
    

    val stream = stream1
    
    def getDStream(n:Int):DStream[SparkFlumeEvent] = {
     if (n > 2){
       var stream2 = FlumeUtils.createStream(ssc, args(n-2), args(n-1).toInt, StorageLevel.MEMORY_ONLY_SER_2)
       getDStream(n-2).union(stream2)
     }else {
       stream
     }
    }
    
    // Print out the count of events received from this server in each batch
//    stream.count().map(cnt => "Received " + cnt + " flume events." ).print()
//    getDStream(args.length).count().map(cnt => "Received " + cnt + " flume events." ).print()

    getDStream(args.length).map(e => e.event.toString()).print()
    ssc.start()
    ssc.awaitTermination()
  }
}