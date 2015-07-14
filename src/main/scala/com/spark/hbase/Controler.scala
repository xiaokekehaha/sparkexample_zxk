package com.spark.hbase

//import org.apache.spark.graphx.VertexId

import scala.collection.mutable.ArrayBuffer

/**
 * Created by xylr on 14-12-3.
 */
class Controler {
  var monitor : ArrayBuffer[String] = _

  def this(monitor : ArrayBuffer[String]){
    this()
    this.monitor = monitor
  }
}
