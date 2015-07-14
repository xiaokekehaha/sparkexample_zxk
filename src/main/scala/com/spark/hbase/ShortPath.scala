package com.spark.hbase

import org.apache.spark.graphx._
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by xylr on 14-12-3.
 */
class ShortPath {

}
object ShortPath {

  def noBelongControlerAndSourceDest(control: ArrayBuffer[String], edge: VertexId,
                                     source: String, dest: String): Boolean = {
    var flag: Boolean = true
    control.map(x =>
      if (edge == x.toLong)
        if (edge == source.toLong || edge == dest.toLong) {} else flag = false)
    flag
  }

  def getShortPath(sc: SparkContext, source: String, dest: String, control: ArrayBuffer[String]
                    ): String = {
    //load the graph
    val graph = GraphLoader.edgeListFile(sc, "/home/xylr/Data/edge.txt")

    //source vertex
    val sourceId: VertexId = source.toLong

    //subgraph noBelongControlerAndSourceDest
    val subgraph = graph.subgraph(vpred = (vid, x) => {
      if (noBelongControlerAndSourceDest(control, vid, source, dest)) true else false
    })

    //the initial function
    val initialGraph = subgraph.mapVertices(
      (id, attr) =>
        if (id == sourceId)
          (0.0, ArrayBuffer[VertexId](sourceId))
        else
          (Double.PositiveInfinity, ArrayBuffer[VertexId]())
    )

    val sssp = initialGraph.pregel(
      //initialMsg the message each vertex will receive at the on the first iteration
      initialMsg = (Double.PositiveInfinity, ArrayBuffer[VertexId]()),
      //the direction of edges
      activeDirection = EdgeDirection.Out)(
        //user-defined vertex program
        (id, dist, newDist) => {
          //          println("vertex function")
          if (dist._1 > newDist._1) newDist else dist
        },
        //sendMsg
        triplet => {
          if (triplet.srcAttr._1 + triplet.attr < triplet.dstAttr._1) {
            //            println((triplet.dstId, (triplet.srcAttr._1 + triplet.attr, triplet.srcAttr._2)))
            Iterator((triplet.dstId, (triplet.srcAttr._1 + triplet.attr, ArrayBuffer[VertexId]() ++= triplet.srcAttr._2 += triplet.dstId)))
          } else
            Iterator.empty
        },
        //mergeMsg
        (a, b) => if (a._1 > b._1) b else a
      )
    var shortPath = new String("")
    val sp = sssp.vertices.collect()
    sp.map(x => {
      if (x._1.toString.equals(dest)) {
        x._2._2.map(y => {
          shortPath = shortPath + y.toString + "->"
        })
      }
    })
    //    println(sssp.vertices.collect().mkString("\n"))
    shortPath = shortPath.substring(0, shortPath.lastIndexOf("->"))
    shortPath
  }
}