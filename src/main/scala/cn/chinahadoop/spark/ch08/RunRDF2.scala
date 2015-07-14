/*
 * Copyright 2015 Sanford Ryza, Uri Laserson, Sean Owen and Joshua Wills
 *
 * See LICENSE file for further information.
 */

package cn.chinahadoop.spark.ch08

import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{ RandomForest, DecisionTree }
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import scala.collection.mutable.Buffer
import org.apache.spark.mllib.clustering._
import org.apache.spark.mllib.linalg._
import org.apache.spark.rdd._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object RunRDF2 {

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("RDF").setMaster("local[2]"))
    val rawData = sc.textFile("file:///D:/sparktestdata/covtype.data/covtype.data")

    val sqlContext = new SQLContext(sc)

    val data22 = sqlContext.jsonRDD(rawData)

    //val data222=sc.
    // val rawData2 = sc.textFile("file:///d:/sparktestdata/keamstestdata.txt", 1)
    val data = Array(1, 2, 3, 4, 5)

    val data222 = List(1, 56, 7, 4).toSeq

    data222.reverse.toArray

    val distData = sc.parallelize(data)

    val testdata = distData.map(a => (a, 1)).reduceByKey(_ + _)

    val aaaa = List(1, 2, 3, 4, 5).zip(List("one", "two", "three", "ooo")).toArray

    for (a <- aaaa) {

      println(a + "===")

    }

    println("====+++++++++++++++++++++++++")

    val datatt = List(1, 2, 3, 4, 5, 7, 8, 10).map {
      var i = 0
      line =>

        i += 1
        (i, line)
      //({i+=1; (i,_)})
    }

    datatt.foreach(println)

    val arr = Array[String]("a", "b", "c")
    val times = new HashMap[String, Int]
   // ("")
    for (i<-arr){
       
        //times(i)=  times.getOrElse(i, 0)+1
      // data=data2
    }


    var xs = Seq((1, 1), (1, 1), (2, 1), (2, 1))
    var test2 = sc.parallelize(xs).reduceByKey((a, b) => 123)
    test2.foreach(println)
    val arr2 = arr(arr.size - 1)

    print("====" + arr2)
    val pairs = sc.makeRDD(arr, 3)

    // val parRDD = new PairRDDFunctions(pairs.map(i=>()))

    val test = pairs.toArray.zipWithIndex.toMap

    for ((k, v) <- test) {

      println(k + "==" + v)

    }

    /*   val labelsAndData = pairs.map { line =>
      val buffer = line.split(',').toBuffer
      buffer.remove(1, 2)
      //val label = buffer.remove(buffer.length - 1)
     // label
    }*/

    //labelsAndData.foreach(println)

    val b = Buffer(1, 2, 3, 34, 6, 5, 5, 6, 6, 7, 7, 7, 7, 7, 8, 6)
    val d = b.remove(1, 2)
    val c = b.remove(b.length - 1)
    val e = b.map(_.toDouble).toArray

    val f = sc.makeRDD(arr, 3)
    println(d)
    println(c)

    e.foreach(println)

    val x = sc.parallelize(Array(Array(1, 2, 3, 4), Array(3, 4, 8), Array(5, 6, 2)))
    println("============++++++====================")
    val dataArray = x.map(_.toArray)

    val x2 = sc.parallelize(Array(ArrayBuffer((3,4),(1,2)), ArrayBuffer((5,6),(7,8))))
    val x3= x2.flatMap(r=>r)
    println("============x3++++++====================")
    x3.collect
    println("============x3++++++====================")
    val dataArray2 = x.map(_.toArray)
    
    val num = dataArray.first.length
    val sums = dataArray.reduce(
      (a, b) => a.zip(b).map(t => t._1 + t._2))
    sums.foreach(println)

    val sums2 = dataArray.fold(new Array[Int](num))(
      (a, b) => a.zip(b).map(t => t._1 + t._2))
    sums2.foreach(println)
    println("============")

    val x1 = sc.parallelize(Array(Array("", ""), Array("", ""), Array("", "")))

    val dataArray1 = x1.map(_.toArray)

    val data1 = dataArray1.map {
      line =>

        var a: String = ""
        val data = line.map {
          line1 =>
            val data = line1
            a = line1
            a
        }

        a

    }

    val data3 = dataArray1.mapPartitionsWithIndex {

      case (index, array) =>

        array.map {

          case line =>

            val data = line.map {

              case line2 =>

                1
            }

        }

    }

    val data2 = dataArray1.map {
      line =>
        for (s <- line) {

        }

    }
    val numCol = dataArray.first().length
    val sumSquares = dataArray.fold(
      new Array[Int](numCol))(
        (a, b) => a.zip(b).map(t => t._1 + t._2 * t._2))

    sumSquares.foreach(println)

    val aa = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
    val bb = aa.map(x => (x.length, x))

    bb.foreach(println)
    val ff = bb.reduce {
      (x, y) => (x._1 + y._1, x._2)

    }
    println("==" + ff)

    //bb.foreach(println)
    val ee = bb.reduceByKey(_ + _).collect
    // ee.foreach(println)
    //val data=pairs.countByValue()

    //   data.foreach(p => println(">>> key=" + p._1 + ", value=" + p._2))
    /*val data = rawData.map { line =>
      val values = line.split(',').map(_.toDouble)
   //   val featureVector = Vectors.dense(values.init)
    
      println("===="+values.last)
      val label = values.last - 1
    //  LabeledPoint(label, featureVector)
      println("++++"+label)
    }*/

  }

}