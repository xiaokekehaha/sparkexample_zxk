package cn.chinahadoop.scala

import scala.util.Random

class RandomTest {

}

object RandomTest extends App{
  
  val  data=List(1,34,5,6)
  
  val  random = new Random
  
  val tt=random.nextInt(data.size)
  
  print(tt)
  
}