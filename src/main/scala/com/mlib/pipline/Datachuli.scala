package com.mlib.pipline
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{ Path, FileSystem }
import org.apache.spark.mllib.feature.{ StandardScalerModel, StandardScaler }
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.SparkContext._
import sun.security.util.Length
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import java.io.File
class Datachuli {

}

object Datachuli extends App {

  val conf = new SparkConf().setAppName("Home Price Recommender").setMaster("local[2]")
  val sc = new SparkContext(conf)

  val app1 = sc.textFile("file:///d:/sparktestdata/sample_libsvm_data3.txt")

  val data = app1.map(_.split(' ')).map {
    line =>
      val data2 = line
      var b = new StringBuilder
      for (x <- data2) {
        if (x.contains(":")) {

          val data3 = x.split(":")

          val data4 = data3(0).toInt * 1000
          b.append(data4).append(":").append(data3(1)).append(" ")
        }

      }
      val data5 = b.deleteCharAt(b.length - 1).toString

      (data2(0) + " " + data5)

  }

  data.foreach(println)
  //sc.textFile("F:\\result").flatMap(line=>line.split("\\n")).map(cloumn => { val v = cloumn.replace("(","").replace(")","").split(","); (v(0),v(1).toInt ) })

  val data6 = data.map(cloumn =>
    {
      val v = cloumn.replace("(", "").replace(")", "")
      v
    })

  val data7 = data6.collect()
  //  val app1 = sc.textFile("file:///d:/sparktestdata/sample_libsvm_data.txt")

  // data6.coalesce(1).saveAsTextFile("file:///d:/123")
  //data.saveAsObjectFile()

  //val data8 = Array("Five","strings","in","a","file!")
  printToFile(new File("d:/example.txt")) { p =>
    data7.foreach(p.println)
  }
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }

  }

}
