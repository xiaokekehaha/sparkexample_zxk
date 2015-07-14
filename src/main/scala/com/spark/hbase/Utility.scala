package com.spark.hbase

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{GraphLoader, VertexId, Graph}

/**
 * Created by xylr on 14-12-3.
 */
object Utility {
  //求子图
  def subGraph(graph: Graph[Int, Int],
               notExist: Array[VertexId]
                ): Graph[Int, Int] = {
    graph
  }

  //导入图数据
  def loadGraph(sc : SparkContext,
                 sourcePath : String): Graph[Int,Int] ={
    val graph = GraphLoader.edgeListFile(sc,sourcePath)
    graph
  }

  //获得车辆行驶方向
  def getDirection(car : Car) : VertexId = {
    1
  }

  //获得车辆行驶路线
  def getShortPath(sc : SparkContext
                    ) : Graph[Int, Int] = {
    val graph = GraphLoader.edgeListFile(sc,"")
    graph
  }
}
