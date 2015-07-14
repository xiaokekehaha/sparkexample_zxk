package com.meizu

class week2 {

}


object week2 {

  

  def add(x: Int, y: Int) = { x + y }
//常用方式
  def add4(x: String): (String => (String, String)) = {

    (line: String) =>
      {

        (x + line + "jj", x + line + "kk")
      }
  }

  def fun2(f: (Int, Int) => Int) = f(1, 2)

  def fun3(f: Double) = (x: Double) => f * x

  def main(args: Array[String]) {
    // 高阶函数 
    add(1, 2) //add方法
    var _add = add _
    //匿名函数
    val fun = (x: Int) => (x + 2)
    fun(10)
    println(fun(10))
    val l = List(1, 4, 6, 8)
    l.map((x: Int) => x * 2) // l.map(_*2)
    println(aa(9))
    //调用function   aa是一个function 方法
    l.map(aa)
    //函数作为参数
    val fun2r = fun2((x: Int, y: Int) => x + y)
    println(fun2r)

    //partial function
    // val asss=gg(1,23,_:Int)    
    val add4result = add3(2)_

    val a = Array(1, 4, 5, 7, 7)
    a.map(_ * 2)
    a.filter(_ > 4)
    a.reduce((x: Int, y: Int) => x + y) //a/reduce(_+_)
    //a.reduceLeft(op)
    //赋值初始值
    a.fold(10)(_ + _)

    //直接调用 test方法
    test(3 > 1)

    print(add4("99")("88"))
  }

  def test(flag: Boolean) {

    println(flag)
  }
  def aa = (x: Int) => 2 * x
  def add2(x: Int, y: Int) = (x + y)
  //颗粒话 方法
  def add3(x: Int)(y: Int) = x + y

}