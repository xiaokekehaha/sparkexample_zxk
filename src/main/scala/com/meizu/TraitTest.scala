package com.meizu

import java.io.File
import scala.io.Source

class TraitTest {

}

object TraitTest extends App{
  
  val s1=new students("1")
  
  val s2=new students("")
  
  val pair=new 	Pair(s1,s2)
  
  val p1=new person("aa")
 
  
  pair.replace(p1)
  
  import Context._
 // new File("").read
  
  1.add2
}

class RichFile(val f:File){
  def  read=Source.fromFile(f.getPath()).mkString
  
}

object Context{
  implicit def file2RichFile(f:File) =new  RichFile(f)

  implicit class HH(x:Int){
    
    def add2=x+2
  }
  
}



class Pair[T](val first : T, val second : T){
 /* def smaller(implicit ord : Ordering[T]) =
    if(ord.compare(first,second) < 0) first else second*/
    
    
    def replace[R >:T](newFirst:R )= new Pair(first,second)
  
  
}



class person(val name :String)

class  students(name :String ) extends person(name)


