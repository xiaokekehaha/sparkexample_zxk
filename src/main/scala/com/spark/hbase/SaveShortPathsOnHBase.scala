package com.spark.hbase

import java.io.{FileInputStream, BufferedInputStream}
import java.util.Properties

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, HTablePool}
import org.apache.hadoop.hbase.util.Bytes

/**
 * Created by xylr on 15-1-20.
 */
class SaveShortPathsOnHBase {

}
object SaveShortPathsOnHBase{
  def getHBaseOfShortPaths(p : Properties) : String = {
    val shortpath = p.getProperty("HBaseShortPaths")
    shortpath
  }

  def getHBaseConfPath(p : Properties) : String = {
    val hbaseConfPath = p.getProperty("HBaseConfPath")
    hbaseConfPath
  }

  def crossingNumber(p : Properties) : String = {
    val cn = p.getProperty("CrossingNumber")
    cn
  }

  def transitTime(p : Properties) : String = {
    val tt = p.getProperty("TransitTime")
    tt
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
  }

  def main(args : Array[String]): Unit ={
    val p = new Properties()
    val in = new BufferedInputStream(new FileInputStream("HBaseInfo.properties"))
    p.load(in)

    setup(p,"17:12","0","10001")
    setup(p,"17:13","0->1","10001")
    setup(p,"17:14","0->1->2","10001")
    setup(p,"17:15","0->1->2->3","10001")
    setup(p,"17:16","0->1->2->3->4","10001")



  }
}
