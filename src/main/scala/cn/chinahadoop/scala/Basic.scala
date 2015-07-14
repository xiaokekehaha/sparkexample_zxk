package cn.chinahadoop.scala

/**
 * Created by chenchao on 14-2-22.
 */
class Basic {

}


object Basic{

  def hello(name : String = "ChinaSpark") : String = { 
    //public static String hello(String name)={ return xxxxx }
    return "Hello : " + name
  }

  def helloScala(){
  var data1="2,m,p,13659842256,nnrhqzwookpwdhe,gxnxqxvorzhxpcde,n,null,go,gj5,l1j4po8,mzkiu0w88ijjxu5x,0tnnrne,r,w,251bn7yoxpkvpyyg,n,x,w,ujiwg4ytgo,jfirf1fgnp,8g,vu,z,w80,2013    1206,094154,734,4,73865579,87817,92746,d,2199592,85866636,912,b,911576,6143,4898,5,2155651578,9211,2296,w,484,857146712,19,y,5274515,2735552713,4897,7741,78814,416    329,28,11448933575,HMsmdsmHsd,mbtz49renoe8u28hvw21q4t7p,p6bs09nl,nysw3b964osfrq8x,1726729543829898,144,7,14,1qt0,u9ia,a9tqsm0,u2socq,20131231,35,20131205,1,138j,4j    qmwk5,e,4x027fi984hrlkkq,acjaynb,b67sjfa,1275,fio,7,147215,4hah4rknwa8h7g,3cqbpn5yz5ogri,cyan79w8l79scr34,8,6331812,xtzxnh8aiykuez,u8tpw1lsi55o3t,74up1skb8vxlzu9r,    4951,557752974,4ylh1jd5m9e052,gtniylb25fh4tz,e56vdy01int7naet,6381,67838656,buf3sk58ygutwn,hlo7huja0vllbv,193ldpuk46z3adrr,5,rs4264,14862283,amxxhx7kzh71mh,lq1lpr1    j,58687875,38azxqjaqlqaoo,a6bahqkdkhp,362378,fn308mo28kuwlu,2p0oa72h,1,5c8zsi8pbgtr6o,o5cv,k,q2cwsp,HMHHHydydsysyd,e,HdysMmymmdmdmy,hjreap,yMsHsHyMdHsHmM,null,20131214"
    val s = data1.split(",");
      val line = s.length match {
        case 124 =>
  println("hello Scala!!!"+s(41))
          if ((!"0".equals(s(41)))) {

          
            s(25) += s(26);
       println("hello Scala!!!"+s(25))
            s(67) = s(83) + s(88) + s(93) + s(98);

            println("hello Scala!!!"+s(0))
            if (s(0) == null) {
               s(0) = "22";
              }
               println("hello Scala!!!"+s(0))         
            if ("2".equals(s(0))) {
              s(0) = "-1";
            }

            var result = new StringBuffer();
            for (i <- 0 until 124) {
             val  s1   = result.append(s(i)).append(",").toString();
             val  s2 = s1.split(",");
                 println("hello Scala!!!"+ s2.length)
            
            }
         println("hello Scala!!!"+result)
          }

      
      
      
     /* var data="00:00:00	06478452674857782	[1000]	4	1	www.1000dy.cn/"
    val s = data.split("\t");
      val line = s.length match {

        case 6 =>
          if(s(3)=="4"){
          val sn ="2";
          s(3)=sn;
            }
       
       var data1  =  s(0) + "\t" + s(1) + "\t" + s(2) + "\t"+s(3)  + "\t" + s(5)
        println("hello Scala!!!"+data)
        println("hello Scala!!!"+data1)*/
      };  
    println("hello Scala!!!")
    
  /*  for(i <- 0 until 124){  //a.b("xxx") === a b "xxx"
     println(i)
    }*/
  }

  val add = (x : Int, y : Int) => x + y

  def add2(x:Int)(y:Int) = x + y

  def printEveryChar(c : String*) = {
   c.foreach(x => println(x))
  }

  def main(args : Array[String]){

    //println("Hello Scala")
    //println(hello("Scala"))
    helloScala()
    //add(1,2)
    //println(add2(1)(2))
    //printEveryChar("a","b","c","d")
    //println(hello())
//    val x = 1
//    val a = if(x > 0) 1 else 0
//    println(a)

//    var (n,r) = (10,0)
//    while(n > 0){
//      r = r + n
//      n = n - 1
//    }
//    println(r)

    //foreach

//    for(i <- 1 until 10){  //a.b("xxx") === a b "xxx"
//      println(i)
//    }

//    for(i <- 1 to 10 if i % 2 == 0){
//      println(i)
//    }

  }
}








