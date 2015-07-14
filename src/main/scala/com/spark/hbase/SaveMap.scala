package com.spark.hbase

import java.io.{FileInputStream, BufferedInputStream}
import java.util.Properties

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, HTablePool}
import org.apache.hadoop.hbase.util.Bytes

import scala.io.Source

/**
 * Created by xylr on 15-1-20.
 */
class SaveMap {

}
object SaveMap{
  def getGZMapFromHBase(p : Properties) : String = {
    val map = p.getProperty("GuangZhouMapInHBase")
    map
  }

  def getGZMapLongitude(p : Properties) : String = {
    val longitude = p.getProperty("GuangZhouMapLongitude")
    longitude
  }

  def getGZMapLatitude(p : Properties) : String = {
    val latitude = p.getProperty("GuangZhouMapLatitude")
    latitude
  }

  def getGZMapGeographicLocation(p : Properties) : String = {
    val geographicLocation = p.getProperty("GuangZhouMapGeographicLocation")
    geographicLocation
  }

  def getHBaseConfPath(p : Properties) : String = {
    val hbaseConfPath = p.getProperty("HBaseConfPath")
    hbaseConfPath
  }

  def getMapFile(p : Properties) : String = {
    val mapfile = p.getProperty("MapFile")
    mapfile
  }

  def getEdgeFile(p : Properties) : String = {
    val edgefile = p.getProperty("EdgeFile")
    edgefile
  }

  def getEdgeHBase(p : Properties) : String = {
    val edgeHBase = p.getProperty("EdgeHBase")
    edgeHBase
  }

  def getEdgeInfoHBase(p : Properties) : String = {
    val edgeInfoHBase = p.getProperty("EdgeInfoHBase")
    edgeInfoHBase
  }

  def loadMap(p : Properties): Unit ={
    val configuration = HBaseConfiguration.create()
    configuration.addResource(getHBaseConfPath(p))


    val pool = new HTablePool()
    val usersTable = pool.getTable(getEdgeHBase(p))
    println("1")

    val sourceFile = Source.fromFile(getEdgeFile(p),"UTF-8")
    val lineIterator = sourceFile.getLines()
    for(i <- lineIterator){
      println(i)
      val temp = i.split("\t")
      //rowKey
      val put = new Put(Bytes.toBytes(temp(0) + "\t" + temp(1)))
      println(temp(0) + "\t" + temp(1))
      put.add(Bytes.toBytes("EdgeInfo"),Bytes.toBytes("Long"),Bytes.toBytes(temp(2)))
      usersTable.put(put)
    }
    usersTable.close()
  }

  def loadGuangZhouMap(p : Properties): Unit ={
    val configuration = HBaseConfiguration.create()
    configuration.addResource(getHBaseConfPath(p))

    val pool = new HTablePool()
    val usersTable = pool.getTable(getGZMapFromHBase(p))

    val sourceFile = Source.fromFile(getMapFile(p),"UTF-8")
    val lineIterator = sourceFile.getLines()
    for(i <- lineIterator){
      val temp = i.split("\t")
      val put = new Put(Bytes.toBytes(temp(0)))
      val longandlat = temp(1).split(" ")
      put.add(Bytes.toBytes(getGZMapGeographicLocation(p)),
        Bytes.toBytes(getGZMapLongitude(p)),Bytes.toBytes(longandlat(0)))
      put.add(Bytes.toBytes(getGZMapGeographicLocation(p)),
        Bytes.toBytes(getGZMapLatitude(p)),Bytes.toBytes(longandlat(1)))

      usersTable.put(put)
    }

    usersTable.close()
  }

//  def getLongitudeAndLatitude(id : Int, p : Properties) : String = {
//    val configuration = HBaseConfiguration.create()
//    configuration.addResource(getHBaseConfPath(p))
//
//    val pool = new HTablePool()
//    val usersTable = pool.getTable(getGZMapFromHBase(p))
//    ""
//  }

  def main(args : Array[String]): Unit ={

    val p = new Properties()
    val in = new BufferedInputStream(new FileInputStream("HBaseInfo.properties"))
    p.load(in)
    //    loadGuangZhouMap(p)
    loadMap(p)


  }
}
