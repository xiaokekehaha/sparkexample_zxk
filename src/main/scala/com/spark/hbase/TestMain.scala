package com.spark.hbase

import java.io.{FileInputStream, BufferedInputStream}
import java.util.{Properties, ArrayList}
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
 * Created by xylr on 14-12-3.
 */
class TestMain {

}
object TestMain{

  def addCar(newCar : String, carList : ArrayBuffer[Car]): Unit ={
    val car = new Car()
    car.LicensePlateNumber = newCar.split("\t")(0)
    car.CrossingNumber = newCar.split("\t")(2)
    car.TransitTime = newCar.split("\t")(1)
    car.TravelingTrack = newCar.split("\t")(2)

    carList += car
  }

  def getCarCrossingNumber(newCar : String, carList : ArrayBuffer[Car]) : String = {
    carList.map(x => {
      if(x.LicensePlateNumber.equals(newCar.split("\t")(0))){
        x.CrossingNumber
      }
    })
    ""
  }

  def getCarTravelingTrack(newCar : String, carList : ArrayBuffer[Car]) : String = {
    carList.map(x => {
      if(x.LicensePlateNumber.equals(newCar.split("\t")(0)))
        x.TravelingTrack
      
    })
    ""
  }

  
    def getCarTravelingTrack2(newCar : String, carList : ArrayBuffer[Car]) : String = {
      var data : String = ""
      carList.map{
      x=>
      if(x.LicensePlateNumber.equals(newCar.split("\t")(0)))
      data =x.TravelingTrack
    }
     data
  }

  def updateCarCrossingNumberAndTravelingTrack(newCar : String,
                                               shortPath : String,
                                               oldTravelingTrack : String,
                                               carList : ArrayBuffer[Car]): Unit ={
    carList.map(x => {
      if(x.LicensePlateNumber.equals(newCar.split("\t")(0))){
        x.CrossingNumber = newCar.split("\t")(2)
        x.TravelingTrack = oldTravelingTrack + shortPath
        x.TransitTime = newCar.split("\t")(1)
      }
    })
  }

  def loadControler(control : ArrayBuffer[String]): Unit ={
    val sourceFile = Source.fromFile("ControlerFile","UTF-8")
    val lineIterator = sourceFile.getLines()
    for(i <- lineIterator){
      control += i
    }
  }

  def getShortPath(sc : SparkContext, sourceCrossingNumber : String , destCrossingNumber : String,
                    control : ArrayBuffer[String]): String ={
    ShortPath.getShortPath(sc,sourceCrossingNumber, destCrossingNumber,control)
  }

  def main(args : Array[String]): Unit ={
    val control = ArrayBuffer[String]()
    loadControler(control)
    control.map(println(_))
  }

  def init(args : Array[String]): Unit ={
//    if (args.length < 3){
//      System.err.println("Usage : TestMain <hostname> <port> <seconds>")
//      System.exit(1)
//    }

    val p = new Properties()
    val in = new BufferedInputStream(new FileInputStream("HBaseInfo.properties"))
    p.load(in)
    //Spark配置文件
    val conf = new SparkConf().setMaster("local[2]").setAppName("TestMain")
//      .setMaster("spark://localhost:7077").setAppName("TestMain")
    //Spark程序入口
    val sc = new SparkContext(conf)
    //SparkStreaming程序入口
//    val ssc = new StreamingContext(conf,Seconds(args(2).toInt))
    val ssc = new StreamingContext(sc,Seconds(2))

    //已经存在数据库中车辆信息
    val list = List("0","1","2")
    val existCarFile = sc.parallelize(list)
    val car = existCarFile.collect()
    //记录已经存在的车牌号
    val arrayBuffer = new ArrayBuffer[String]()
    arrayBuffer ++= car

    //记录已经存在的车辆的车牌号和行驶轨迹,还有时间
    val carList = new ArrayBuffer[Car]()

    //监控路口编号
    val control = new ArrayBuffer[String]()
    loadControler(control)

    //获取新的数据流
//    val lines = ssc.socketTextStream(args(0),args(1).toInt)
    val lines = ssc.socketTextStream("localhost",9999)
    //从获取到的数据流片段中分离出车辆信息
    val words = lines.flatMap(_.split(" "))
    //判断是否为新车辆,如果是,添加到数据库中.如果不是,计算车辆轨迹,添加到数据库
    val word = words.map(newCar => {
        arrayBuffer.map(oldCarList => {
          if(BlackListOfCar.checkExistInBlackList(p, newCar.split("\t")(0))){
          }
          else{
            //旧车辆--计算新的轨迹
            if(oldCarList == newCar.split("\t")(0)){
              println("exist")
              val crossNumber = getCarCrossingNumber(newCar, carList)
              val oldTravelingTrack = getCarTravelingTrack(newCar, carList)
              println("计算新的轨迹")
              val shortpath = getShortPath(sc, crossNumber, newCar.split("\t")(2), control)
              updateCarCrossingNumberAndTravelingTrack(newCar, shortpath, oldTravelingTrack, carList)
            }
            //新车辆--添加数据库
            else{
              println("noexist")
              println("添加新车辆")
              addCar(newCar,carList)
              //更新已经存在的车牌号
              arrayBuffer += newCar.split("\t")(0)
            }
          }

        })
      })
    val cont = word.saveAsTextFiles("/home/xylr/project/spark/streaming/result/")

    ssc.start()
    ssc.awaitTermination()
  }
}
