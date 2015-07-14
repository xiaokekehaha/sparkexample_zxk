package com.mlib.pipline
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{ LogisticRegressionWithLBFGS, LogisticRegressionModel }
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
class Logisticregression {}
object Logisticregression extends App {

  val conf = new SparkConf().setAppName("dataframe").setMaster("local[4]")
  val sc = new SparkContext(conf)

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  // Load training data in LIBSVM format.
  val data = MLUtils.loadLibSVMFile(sc, "d:/sparktestdata/sample_libsvm_data.txt",600)
  //D:\sparktestdata\DecisionTree-master\data
  // Split data into training (60%) and test (40%).
  val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
  val training = splits(0).cache()
  val test = splits(1)

  // Run training algorithm to build the model
  /*val model = new LogisticRegressionWithLBFGS()
    .setNumClasses(10)
    .run(training)*/

  val numIterations = 20
  val model = LogisticRegressionWithSGD.train(training, numIterations)

 println("==="+ model.weights.toArray.length)
  //println("======"+model.numFeatures)

/*  println(model.toString)

  val labelAndPreds = training.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }*/
  /*val data2=  model.weights.toArray
 //  model.
  for(x <-data2){
    
    print("=="+x)
  }*/
  //model.weights.toArray.foreach(println) 
  // println(model.userFeatures.mapValues(_.mkString(", ")).first())   
  // Compute raw scores on the test set.

  /*println("============================================================================")
  val predictionAndLabels = test.map {
    case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
  }

  predictionAndLabels.foreach(println)*/
  // Get evaluation metrics.
  //  val metrics = new MulticlassMetrics(predictionAndLabels)
  //  val precision = metrics.precision
  // println("Precision = " + precision)

  // Save and load model
  //model.save(sc, "myModelPath")
  //val sameModel = LogisticRegressionModel.load(sc, "myModelPath")
}