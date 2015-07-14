package cn.chinahadoop.spark

import org.apache.spark.{ SparkContext, SparkConf }
import scala.collection.mutable.ListBuffer
import org.apache.spark.SparkContext._

/**
 * Created by chenchao on 14-3-1.
 */
class test {

}

object test {

  def main(args: Array[String]) {

 		 val sc = new SparkContext("local[2]", "WordCount", System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_TEST_JAR")))
         val textFile = sc.textFile("file:///d:/riyongruanjian/test.txt")
         val result = textFile.flatMap(line => line.split("\t")).map(word => (word, 1)).reduceByKey(_ + _)
          print("结果"+ result.take(2))  
  
          
       /*  val t2 = sc.sequenceFile("file:///d:/12.seq-r-00000",classOf[String], classOf[String]) 
          t2.groupByKey().take(5) */

    
    /*val conf = new SparkConf().setAppName("GroupByKey").setMaster("local[2]")
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("D:\\")

    val data = Array[(Int, Char)]((1, 'a'), (2, 'b'),
      (3, 'c'), (4, 'd'),
      (5, 'e'), (3, 'f'),
      (2, 'g'), (1, 'h')

    )
    val pairs = sc.parallelize(data, 3)

   // pairs.checkpoint
    pairs.count

    val result = pairs.groupByKey(2)

    // output:
    //pairs.foreachWith(i => i)((x, i) => println("[dataPartitionIndex " + i + "] " + x))
    result.foreachWith(i => i)((x, i) => println("[PartitionIndex " + i + "] " + x))

    println(result.toDebugString)
*/
    /*
  [dataPartitionIndex 0] (1,a)
  [dataPartitionIndex 0] (2,b)

  [dataPartitionIndex 1] (3,c)
  [dataPartitionIndex 1] (4,d)
  [dataPartitionIndex 1] (5,e)

  [dataPartitionIndex 2] (3,f)
  [dataPartitionIndex 2] (2,g)
  [dataPartitionIndex 2] (1,h)

  [PartitionIndex 0] (4,ArrayBuffer(d))
  [PartitionIndex 0] (2,ArrayBuffer(b, g))

  [PartitionIndex 1] (1,ArrayBuffer(a, h))
  [PartitionIndex 1] (3,ArrayBuffer(c, f))
  [PartitionIndex 1] (5,ArrayBuffer(e))

  MappedValuesRDD[3] at groupByKey at groupByKeyTest.scala:19 (2 partitions)
    MapPartitionsRDD[2] at groupByKey at groupByKeyTest.scala:19 (2 partitions)
      ShuffledRDD[1] at groupByKey at groupByKeyTest.scala:19 (2 partitions)
        ParallelCollectionRDD[0] at parallelize at groupByKeyTest.scala:17 (3 partitions)
  */
  }

}















