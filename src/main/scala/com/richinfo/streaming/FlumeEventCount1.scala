/*package com.richinfo.streaming

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.flume._
import org.apache.spark.streaming.dstream.DStream

*//**
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
 *//*
object FlumeEventCount1 {
  def main(args: Array[String]) {
    
      val batchInterval = Milliseconds(20000)
     val ssc = new StreamingContext("spark://192.168.7.127:7077", "streaming", batchInterval,
      System.getenv("SPARK_HOME"))
  
    val lines = ssc.socketTextStream(args(0),args(1).toInt)
    //从获取到的数据流片段中分离出车辆信息
    val words = lines.flatMap(_.split(" "))
    val word = words.map(newCar => {
     newCarFlag = false
      arrayBuffer.map(oldCarList => {
         if(newCar.split("\t")(0) == oldCarList){
         }
         else{}
           newCarFlag = true
      })
      val graph = GraphLoader.edgeListFile(ssc.sparkContext, "/home/xylr/Data/edge.txt")
      graph.cache()
      graph.vertices.collect()
      if(newCarFlag){
        println("newCar")
        addCar(newCar,carList)
        arrayBuffer += newCar.split("\t")(0)
        println(graph.vertices.count())
        updateCarCrossingNumberAndTravelingTrack(p,newCar,
          getShortPath(graph,"1","2",control)"1",getCarTravelingTrack(newCar,carList),carList)
      }
      



def updateCarCrossingNumberAndTravelingTrack(p : Properties,
                                               newCar : String,
                                               shortPath : String,
                                               oldTravelingTrack : String,
                                               carList : ArrayBuffer[Car]): Unit ={
    carList.map(x => {
      if(x.LicensePlateNumber.equals(newCar.split("\t")(0))){
        x.CrossingNumber = newCar.split("\t")(2)
        x.TravelingTrack = oldTravelingTrack + shortPath
        x.TransitTime = newCar.split("\t")(1)
        println(newCar)
        new SaveShortPathsOnHBase().setup(p,x.TransitTime,x.TravelingTrack,x.CrossingNumber)
      }
    })
  }

def setup(p : Properties, time : String, shortpath : String, id : String): Unit ={
    val configuration = HBaseConfiguration.create()
    configuration.addResource(getHBaseConfPath(p))

    val pool = new HTablePool()
    val usersTable = pool.getTable(getHBaseOfShortPaths(p))

    //the rowkey (id)
    val put = new Put(Bytes.toBytes(id))

    var cn = ""

    if(shortpath.length == 1){
      cn = shortpath
    }else{
      cn = shortpath.substring(shortpath.lastIndexOf("->")+"->".length,shortpath.length)
    }

    put.add(Bytes.toBytes(crossingNumber(p)),Bytes.toBytes(cn),Bytes.toBytes(shortpath))
    put.add(Bytes.toBytes(transitTime(p)),Bytes.toBytes(time),Bytes.toBytes(shortpath))


    usersTable.put(put)
    usersTable.close()
  }*/