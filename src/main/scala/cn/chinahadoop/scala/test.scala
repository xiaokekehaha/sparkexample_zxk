package cn.chinahadoop.scala

class test {

}

object test  extends App{
  val days = Array("Sunday","Sunday", "Monday", "Tuesday", "Wednesday",
"Thursday", "Friday", "Saturday")
  
days.zipWithIndex.foreach {
case(day, count) => println(s"$count is $day")
}
println("hello world")


val bag = List("1", "2", "three", "4", "one hundred seventy five")

def toInt(in: String): Option[Int] = {
try {
Some(Integer.parseInt(in.trim))
} catch {
case e: Exception => None
}
}

val data=bag.flatMap(toInt(_)).sum
println(data)

val list = "apple" :: "banana" :: 1 :: 2 :: Nil

val strings=list.filter{
  case s:String=>true
  case _=>false 
}

def onlystring(a:Any)=a match {
  case s:String =>true 
  case _=>false
}

val data2=list.filter(onlystring).foreach(println)



}


