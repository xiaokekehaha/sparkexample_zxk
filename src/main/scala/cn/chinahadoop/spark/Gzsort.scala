package cn.chinahadoop.spark


package cn.chinahadoop.spark

import org.apache.spark.{ SparkContext, SparkConf }
import scala.collection.mutable.ListBuffer
import org.apache.spark.SparkContext._

/**
 * Created by zxk on 14-3-1.
 */
class Gzsort {

}

object Gzsort {

  def main(args: Array[String]) {

 	
         if (args.length != 2) {
           println("参数出错")
           System.exit(0)
           }
       val conf = new SparkConf().setAppName("GzSort")
    
    	val sc = new SparkContext(conf);

		val file = sc.textFile(args(0))
		
	 val sortReslut=  file.map(_.split('\t')).map(x => (x(0)+","+x(3),x(1).toInt)).reduceByKey(_+_).map(x => (x._2,x._1)).sortByKey(false).saveAsTextFile(args(1))
		
		sc.stop()
  }

}















