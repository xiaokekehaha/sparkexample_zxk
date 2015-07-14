package cn.chinahadoop.spark.ch08
import scala.collection.SortedSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.{ListBuffer, HashMap}
class exercise02 {

}
object exercise02 extends App{
  
  def mapStrIndex(str:String)={
  var indexMap = new HashMap[Char,SortedSet[Int]]()
  var i = 0
  str.toCharArray.foreach {
    c =>
      indexMap.get(c) match {
        case Some(result) => indexMap(c) = result + i
        case None => indexMap += (c -> SortedSet {
          i
        })
      }
      i += 1
  }
  indexMap

}
  def mapStrIndex1(str:String)={
  var indexMap = new HashMap[Char,SortedSet[Int]]()
  var i = 0
  str.toCharArray.foreach {
    c =>
      indexMap.get(c) match {
        case Some(result) => indexMap(c) = result + i
        case None => indexMap += (c -> SortedSet {
          i
        })
      }
      i += 1
  }
  indexMap

}
println(mapStrIndex1("Mississippi"))
  
println(mapStrIndex("Mississippi"))
}