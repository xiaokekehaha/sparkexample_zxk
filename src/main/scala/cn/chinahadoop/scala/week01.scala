package cn.chinahadoop.scala
class week01 {

}

object week01 {


    def add( x:Int, y:Int ) :Int= {
    
     x+y
     }
  
    def d(b:(String,String)=>String):String=b("aa","aa")
 def main(args : Array[String]){
   
   var result= add(1,2)
    
    println("=="+result)
    
    val a=(x:Int)=>x+1
    val c=(b:(String,String)=>String)=>b("aa","aa")
    val b=(x:Int)=>(y:Int)=>x+y
    def add1(x:Int)(y:Int)(z:Int) = x+y+z 
  def f(x:Int, y:Int, m:(Int, Int)=>Int) = m(x,y)

   


    println("=="+result+"=="+c+"=="+add1(1)(2)(3))
    
    println("=="+result+"=="+b(1)(2)+"=="+  f(3,4, (x,y)=>x+y) )// 7)
    
      
    }
}