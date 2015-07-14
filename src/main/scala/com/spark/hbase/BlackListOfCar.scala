package com.spark.hbase

import java.io.{FileInputStream, BufferedInputStream}
import java.util.Properties

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Get, HTablePool}
import org.apache.hadoop.hbase.util.Bytes
//import org.eclipse.jdt.internal.compiler.lookup.ProblemPackageBinding

/**
 * Created by xylr on 15-1-20.
 */
class BlackListOfCar {

}
object BlackListOfCar{
  def getHBaseConfPath(p : Properties) : String = {
    val hbaseConfPath = p.getProperty("HBaseConfPath")
    hbaseConfPath
  }
  def getBlackListOnHBase(p : Properties) : String = {
    val BlackListOnHBase = p.getProperty("BlackListOnHBase")
    BlackListOnHBase
  }
  def checkExistInBlackList(p : Properties, newCar : String) : Boolean = {
    val configuration = HBaseConfiguration.create()
    configuration.addResource(getHBaseConfPath(p))

    val pool = new HTablePool()
    println(1)
    println(getBlackListOnHBase(p))
    val usersTable = pool.getTable(getBlackListOnHBase(p))
    println(2)
    val g = new Get(Bytes.toBytes(newCar))
    val r = usersTable.get(g)
    println(r)
    false
  }
  def main(args : Array[String]): Unit ={
    val p = new Properties()
    val in = new BufferedInputStream(new FileInputStream("HBaseInfo.properties"))
    p.load(in)
    checkExistInBlackList(p,"10000")
  }
}
