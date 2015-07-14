package com.meizu

import scala.collection.Map
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

class ShortCarTest {

}

object ShortCarTest{
  
  def main(args: Array[String]) {
    // 设置运行环境
    val conf = new SparkConf().setAppName("Kmeans").setMaster("local[2]")
    val sc = new SparkContext(conf)

     //装载数据集
    //  val rawData = sc.textFile("file:///d:/keamstestdata.txt", 1)
    val carData = sc.textFile("file:///d:/sparktestdata/car/car.txt")
    
   val data= carDatacollect(carData).reduceByKey((x,y)=>x+":"+y)
   
   val data2=data.map(_._2.split(":"))
   
   
    
    
}
  def carDatacollect(carData:RDD[String])={
    
    carData.map{
      line =>
     val data=line.split(",")
     
     val carid=data(0)
     
     val data1=data.slice(1, data.length-1).mkString(" ")
     
     (carid,data1)
      
    }
      
  }
  
}