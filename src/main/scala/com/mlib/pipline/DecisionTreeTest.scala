package com.mlib.pipline

import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{ Row, SQLContext }
import org.apache.spark.mllib.linalg.{ Vector, Vectors }
class DecisionTreeTest {

}

object DecisionTreeTest extends App{

  val conf = new SparkConf().setAppName("dataframe").setMaster("local[4]")
  val sc = new SparkContext(conf)

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
 // val data = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")
  
   /* val training = sc.parallelize(Seq(
    LabeledPoint(1.0, Vectors.dense(0.0, 1.1, 0.1)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.0, -1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(1.0, Vectors.dense(0.0, 1.2, -0.5))))*/
  //D:\sparktestdata  D:\sparktestdata  sample_libsvm_data.txt
  
  //  val training = MLUtils.loadLibSVMFile(sc, "hdfs://mz-hadoop-1.meizu.com:9000/zhouxiaoke/sample_libsvm_data.txt")
   val training = MLUtils.loadLibSVMFile(sc, "file:///d:/sparktestdata/sample_libsvm_data3.txt")
   
   
  // Split the data into training and test sets (30% held out for testing)
  val splits = training.randomSplit(Array(0.6, 0.4), 11l)
  val (trainingData, testData) = (splits(0), splits(1))

  val numClasses = 2
  val categoricalFeaturesInfo = Map[Int, Int]()
  val impurity = "gini"
  val maxDepth = 5
  val maxBins = 2

  //val data=trainingData
  val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
    impurity, maxDepth, maxBins)

  // Evaluate model on test instances and compute test error
  val labelAndPreds = testData.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }
  
  
  println("===="+labelAndPreds.foreach{case (x,y)=>println(x,y)})
  val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
 // println("Test Error = " + testErr)
 // println("Learned classification tree model:\n" + model.toDebugString)
}