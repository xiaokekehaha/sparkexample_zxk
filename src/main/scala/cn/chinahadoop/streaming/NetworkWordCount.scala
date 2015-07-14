package cn.chinahadoop.streaming

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.spark.storage.StorageLevel
import org.apache.spark.SparkConf
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.rdd.RDD
import scala.util.parsing.json.JSON
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{ RandomForest, DecisionTree }
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import scala.collection.mutable.Buffer
import org.apache.spark.mllib.linalg._
import org.apache.spark.rdd._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext
case class AlertMsg(host: String, count: Int, sum: Double)

/**
 * Chen Chao
 */
object NetworkWordCount {
  def main(args: Array[String]) {

    // 新建StreamingContext
    val ssc = new StreamingContext(args(0), "NetworkWordCount", Seconds(args(3).toInt),
      System.getenv("SPARK_HOME"))

    val lines = ssc.socketTextStream(args(1), args(2).toInt, StorageLevel.MEMORY_AND_DISK)
    val words = lines.flatMap(_.split(" "))

    val sc = new SparkContext(new SparkConf().setAppName("RDF").setMaster("local[2]"))
    val sqlContext = new SQLContext(sc)

    val data = words.map(JSON.parseFull(_))

    words.foreachRDD {

      line =>
        val data = sqlContext.jsonRDD(line)

    }

    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()

    // val test=lines.transform(test2)

    //wordCounts.saveAsTextFiles("hdfs://five01:9000/spark/result/hdfswordcount")
    ssc.start()
    ssc.awaitTermination()
    ssc.start()
    ssc.awaitTermination()

  }

  def test2(test: RDD[String]) = {

    test.map(_.split("")(0))
  }

}

