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
import org.apache.velocity.runtime.directive.Foreach
import org.apache.spark.mllib.stat.Statistics
import scala.util.Random

object RunRDF {

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("RDF").setMaster("local[2]"))
    val rawData = sc.textFile("file:///D:/sparktestdata/covtype.data/covtype.data")

    val data_path = "/home/jb-huangmeiling/sample_stat.txt"
    val data2 = sc.textFile(data_path).map(_.split("\t")).map(f => f.map(f => f.toDouble))

    val data1 = data2.map(f => Vectors.dense(f))
    //计算每列最大值、最小值、平均值、方差值、L1范数、L2范数
    val stat1 = Statistics.colStats(data1)
    stat1.max
    stat1.min
    stat1.mean
    stat1.variance
    stat1.normL1
    stat1.normL2

    val input = args.length match {
      case x: Int if x > 1 => sc.textFile(args(1))
      case _ => sc.parallelize(List("pandas", "i like pandas"))
    }
    val words = input.flatMap(line => line.split(" "))

    val data = rawData.map { line =>
      val values = line.split(',').map(_.toDouble)
      val featureVector = Vectors.dense(values.init)
      val label = values.last - 1
      LabeledPoint(label, featureVector)
    }

    // Split into 80% train, 10% cross validation, 10% test
    val Array(trainData, cvData, testData) = data.randomSplit(Array(0.7, 0.2, 0.1))
    trainData.cache()
    cvData.cache()
    testData.cache()

    simpleDecisionTree(trainData, cvData)
    randomClassifier(trainData, cvData)
    evaluate(trainData, cvData, testData)
    evaluateCategorical(rawData)
    evaluateForest(rawData)

    elements(trainData)
    trainData.unpersist()
    cvData.unpersist()
    testData.unpersist()
  }

  def elements(trainData: RDD[LabeledPoint]) = {
    val data = trainData.map {

      line =>
        val lable = line.label
        val featrues = line.features
        (lable, featrues.toArray)
    }

  }
  def simpleDecisionTree(trainData: RDD[LabeledPoint], cvData: RDD[LabeledPoint]): Unit = {
    // Build a simple default DecisionTreeModel
    val model = DecisionTree.trainClassifier(trainData, 7, Map[Int, Int](), "gini", 4, 100)

    val metrics = getMetrics(model, cvData)

    println("==" + metrics.confusionMatrix)
    println(metrics.precision)

    val data = (0 until 7).map(
      category => (metrics.precision(category), metrics.recall(category)))
    (0 until 7).map(
      category => (metrics.precision(category), metrics.recall(category))).foreach(println)
  }

  def getMetrics(model: DecisionTreeModel, data: RDD[LabeledPoint]): MulticlassMetrics = {
    val predictionsAndLabels = data.map(example =>
      (model.predict(example.features), example.label))

    predictionsAndLabels.foreach(println)
    new MulticlassMetrics(predictionsAndLabels)
  }

  def randomClassifier(trainData: RDD[LabeledPoint], cvData: RDD[LabeledPoint]): Unit = {
    val trainPriorProbabilities = classProbabilities(trainData)
    //println("====="+trainPriorProbabilities)
    for (x <- trainPriorProbabilities) {
      println("====" + x)
    }
    val cvPriorProbabilities = classProbabilities(cvData)

    //val accuracy1 = trainPriorProbabilities.zip(cvPriorProbabilities)
    val accuracy = trainPriorProbabilities.zip(cvPriorProbabilities).map {
      case (trainProb, cvProb) => trainProb * cvProb
    }.sum
    println(accuracy)
  }

  def classProbabilities(data: RDD[LabeledPoint]): Array[Double] = {
    // Count (category,count) in data
    val countsByCategory = data.map(_.label).countByValue()
    // order counts by category and extract counts	
    val counts = countsByCategory.toArray.sortBy(_._1).map(_._2)
    counts.map(_.toDouble / counts.sum)
  }

  def evaluate(
    trainData: RDD[LabeledPoint],
    cvData: RDD[LabeledPoint],
    testData: RDD[LabeledPoint]): Unit = {

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
      trainData.union(cvData), 7, Map[Int, Int](), "entropy", 20, 300)
    println(getMetrics(model, testData).precision)
    println(getMetrics(model, trainData.union(cvData)).precision)
  }

  def unencodeOneHot(rawData: RDD[String]): RDD[LabeledPoint] = {
    rawData.map { line =>
      val values = line.split(',').map(_.toDouble)
      // Which of 4 "wilderness" features is 1
      val wilderness = values.slice(10, 14).indexOf(1.0).toDouble
      // Similarly for following 40 "soil" features
      val soil = values.slice(14, 54).indexOf(1.0).toDouble
      // Add derived features back to first 10
      val featureVector = Vectors.dense(values.slice(0, 10) :+ wilderness :+ soil)
      val label = values.last - 1
      LabeledPoint(label, featureVector)
    }
  }

  def evaluateCategorical(rawData: RDD[String]): Unit = {

    val data = unencodeOneHot(rawData)

    val Array(trainData, cvData, testData) = data.randomSplit(Array(0.8, 0.1, 0.1))
    trainData.cache()
    cvData.cache()
    testData.cache()

    val evaluations =
      for (
        impurity <- Array("gini", "entropy");
        depth <- Array(10, 20, 30);
        bins <- Array(40, 300)
      ) yield {
        // Specify value count for categorical features 10, 11
        val model = DecisionTree.trainClassifier(
          trainData, 7, Map(10 -> 4, 11 -> 40), impurity, depth, bins)
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

  def evaluateForest(rawData: RDD[String]): Unit = {

    val data = unencodeOneHot(rawData)

    val Array(trainData, cvData) = data.randomSplit(Array(0.9, 0.1))
    trainData.cache()
    cvData.cache()

    val forest = RandomForest.trainClassifier(
      trainData, 7, Map(10 -> 4, 11 -> 40), 20, "auto", "entropy", 30, 300)

    val predictionsAndLabels = cvData.map { example =>

      // val data=1

      (forest.predict(example.features), example.label)
    }

    println(new MulticlassMetrics(predictionsAndLabels).precision)

    val input = "2709,125,28,67,23,3224,253,207,61,6094,0,29"
    val vector = Vectors.dense(input.split(',').map(_.toDouble))
    println(forest.predict(vector))
  }

  def saveLableData(data: RDD[LabeledPoint], dir: String) = {

    val datas = data.map {
      x =>
        x.label + "," + x.features.toArray.mkString(" ")
    }
    datas.saveAsTextFile(dir)
  }

  /*  def savesvmData(data:RDD[LabeledPoint],dir:String)={
    
   val datas= data.map{
      case LabeledPoint(lable,features)=>
        val sb=new StringBuilder(lable.toString)  
        
        features.foreachActive{
          case(x,y)=>
            sb+=""
            sb++=s"${x+1}:$v"  
        }
        sb.mkString
    }
   datas.saveAsTextFile(dir) 
    
  }*/

  def generateRdd(sc: SparkContext, numbers: Int, parts: Int, fnumber: Int) = {

    val data = sc.parallelize(0 until numbers, parts).map {
      line =>
        val random = new Random(42 + line)
        val y = if (line % 2 == 0) 0.0 else 1.0
        val x = Array.fill[Double](fnumber) {
          random.nextGaussian + (y * 2)
        }

        LabeledPoint(y, Vectors.dense(x))
    }
    data

  }
}