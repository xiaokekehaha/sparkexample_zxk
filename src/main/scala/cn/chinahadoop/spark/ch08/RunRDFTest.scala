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
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
object RunRDFTest {

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("RDF").setMaster("local[2]"))
    val rawData = sc.textFile("file:///D:/sparktestdata/covtype.data/decision.txt")

    val data = rawData.map {
      line =>
        val data1 = line.split(",").map(_.toDouble)

        //data1.foreach(println)

        val featureVector = Vectors.dense(data1.init)
        val valueLable = data1.last - 1
        LabeledPoint(valueLable, featureVector)
    }

    val Array(trainData, cvData, testData) = data.randomSplit(Array(0.8, 0.1, 0.1))

    trainData.cache()
    cvData.cache()
    testData.cache()

    simpleDecisionTree(trainData, cvData)

    //randomClassifier(trainData, cvData)
    evaluate(trainData, cvData, testData)
   // evaluateCategorical(rawData)
  }

  def evaluateCategorical(rawData: RDD[String]): Unit = {

    val data = unencodeOneHot(rawData)
    
    //
    val data2=data.map(_.features).map(_.toArray)
    
    
    val numCols = data2.first()
     numCols.foreach{x=> print(x+",")}
    
/*    data2.foreach(
     a=>{
      a.foreach(e=>print(e+",")) 
      println() 
     }
    )*/
    
    val Array(trainData, cvData, testData) = data.randomSplit(Array(0.8, 0.1, 0.1))
    trainData.cache()
    cvData.cache()
    testData.cache()

     val evaluations =
      for (impurity <- Array("gini", "entropy");
           depth    <- Array(10, 20);
           bins     <- Array(10, 300))
      yield {
        // Specify value count for categorical features 10, 11
        val model = DecisionTree.trainClassifier(
          trainData, 7, Map[Int, Int](), impurity, depth, bins)
        val trainAccuracy = getMetrics(model, trainData).precision
        val cvAccuracy = getMetrics(model, cvData).precision
        // Return train and CV accuracy
        ((impurity, depth, bins), (trainAccuracy, cvAccuracy))
      }
/*    val evaluations =
      for (
        impurity <- Array("gini", "entropy");
        depth <- Array(1, 20);
        bins <- Array(10, 300)
      ) yield {
        val model = DecisionTree.trainClassifier(
          trainData, 7, Map[Int, Int](), impurity, depth, bins)
        val accuracy = getMetrics(model, cvData).precision
        ((impurity, depth, bins), accuracy)
      }*/
    evaluations.sortBy(_._2._2).reverse.foreach(println)
    
  }

  def evaluateCategorical1(rawData: RDD[String]): Unit = {
    
    val data = unencodeOneHot(rawData)

    val Array(trainData, cvData, testData) = data.randomSplit(Array(0.8, 0.1, 0.1))
    trainData.cache()
    cvData.cache()
    testData.cache()

    val evaluations =
      for (impurity <- Array("gini", "entropy");
           depth    <- Array(10, 20, 30);
           bins     <- Array(4, 8))
      yield {
        // Specify value count for categorical features 10, 11
        val model = DecisionTree.trainClassifier(
          trainData, 7, Map(10 -> 4, 11 -> 4), impurity, depth, bins)
        val trainAccuracy = getMetrics(model, trainData).precision
        val cvAccuracy = getMetrics(model, cvData).precision
        // Return train and CV accuracy
        ((impurity, depth, bins), (trainAccuracy, cvAccuracy))
      }

    evaluations.sortBy(_._2._2).reverse.foreach(println)

    val model = DecisionTree.trainClassifier(
      trainData.union(cvData), 7, Map(10 -> 4, 11 -> 40), "entropy", 30, 300)
    println(getMetrics(model, testData).precision)

    trainData.unpersist()
    cvData.unpersist()
    testData.unpersist()
  }
   
  def unencodeOneHot(rawData: RDD[String]): RDD[LabeledPoint] = {

    rawData.map {
      line =>
        
        
        val values = line.split(',').map(_.toDouble)
        val wilderness = values.slice(10, 14).indexOf(1.0).toDouble
        // Similarly for following 40 "soil" features
        val soil = values.slice(14, 54).indexOf(1.0).toDouble
        // Add derived features back to first 10
        val featureVector = Vectors.dense(values.slice(0, 10) :+ wilderness :+ soil)
        val label = values.last - 1
        LabeledPoint(label, featureVector)

    }
  }
  def evaluate(trainData: RDD[LabeledPoint], cvData: RDD[LabeledPoint], testData: RDD[LabeledPoint]) = {

    val evaluations =
      for (
        impurity <- Array("gini", "entropy");
        depth <- Array(1, 20);
        bins <- Array(10, 300)
      ) yield {
        val model = DecisionTree.trainClassifier(
          trainData, 7, Map[Int, Int](), impurity, depth, bins)
        val accuracy = getMetrics(model, cvData).precision
        ((impurity, depth, bins), accuracy)
      }

    evaluations.sortBy(_._2).reverse.foreach(println)
    val model = DecisionTree.trainClassifier(
      trainData.union(cvData), 7, Map[Int, Int](), "entropy", 10, 20)
 //   println("==" + getMetrics(model, testData).precision)
   // println("++" + getMetrics(model, trainData.union(cvData)).precision)

  }
  def randomClassifier(trainData: RDD[LabeledPoint], cvData: RDD[LabeledPoint]) = {
    val trainPriorProbabilities = classProbabilities(trainData)

    val cvPriorProbabilities = classProbabilities(cvData)

    val accuracy = trainPriorProbabilities.zip(cvPriorProbabilities).map {
      case (trainProb, cvProb) => trainProb * cvProb
    }.sum
    println(accuracy)

  }

  def classProbabilities(trainData: RDD[LabeledPoint]): Array[Double] = {

    // Count (category,count) in data
    val countsByCategory = trainData.map(_.label).countByValue()

    countsByCategory.foreach(println)

    // order counts by category and extract counts
    val counts = countsByCategory.toArray.sortBy(_._1).map(_._2)

    val data = counts.map(_.toDouble / counts.sum)
    data.foreach(println)

    counts.map(_.toDouble / counts.sum)

  }

  def simpleDecisionTree(trainData: RDD[LabeledPoint], cvData: RDD[LabeledPoint]) = {

    val model = DecisionTree.trainClassifier(trainData, 7, Map[Int, Int](), "gini", 4, 10)

    val metrics = getMetrics(model, cvData)
    println(metrics.confusionMatrix)
    println(metrics.precision)
  //   println(metrics.precision(1))
    /*(0 until 7).map(
      category => (metrics.precision(category), metrics.recall(category))).foreach(println)*/

  }

  def getMetrics(model: DecisionTreeModel, cvData: RDD[LabeledPoint]): MulticlassMetrics = {

   // val data=DecisionTreeModel.label
    
    val predictionsAndLabels = cvData.map {
      line =>
        (model.predict(line.features), line.label)
    }

    
   // val metrics = new BinaryClassificationMetrics(predictionsAndLabels)
    
    new MulticlassMetrics(predictionsAndLabels)

  }
  
  


}