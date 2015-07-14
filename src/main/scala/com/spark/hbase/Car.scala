package com.spark.hbase

//import org.apache.spark.graphx.VertexId

import scala.collection.mutable.ArrayBuffer

/**
 * Created by xylr on 14-12-3.
 */
class Car {
  //车牌号
  var LicensePlateNumber : String = _
  //行驶方向
  var DrivingDirection : String = _
  //通过的路口编号
  var CrossingNumber : String = _
  //通过的时间
  var TransitTime : String = _
  //车辆行驶轨迹
  var TravelingTrack : String = _

  def this(LicensePlateNumber : String){
    this()
    this.LicensePlateNumber = LicensePlateNumber
  }
}
object Car{

  def add(car : ArrayBuffer[Car]): Unit ={
    val c = new Car("1")
    car += c
  }

  def main(args : Array[String]): Unit ={
    val car = new ArrayBuffer[Car]()

    println(car.length)
    add(car)
    println(car.length)


//    val cars = new Car("1")
//    cars.CrossingNumber = 0
//    println(cars.DrivingDirection)
//    println(cars.LicensePlateNumber)

  }
}
