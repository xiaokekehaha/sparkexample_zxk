package cn.chinahadoop.mlibtest1


import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils
/**
 * Created by xuhong on 2015/4/14.
 */
object SvmModel {

  val sparkConf: SparkConf = new SparkConf()
  val sc: SparkContext = new SparkContext(sparkConf)


  val splitChar: String = ","
  val trainRDD1: RDD[String] = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/gender-train-dataset").mapPartitions(iter => {
    new Iterator[String]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): String = {
        val line: String = iter.next()
        line.replace("(", "").replace(")", "")
      }
    }
  })
  val trainRDD2: RDD[LabeledPoint] = trainRDD1.mapPartitions(iter => {
    new Iterator[LabeledPoint]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): LabeledPoint = {
        val line: String = iter.next()
        val array: Array[Double] = line.split(splitChar).map(_.toDouble)
        val features: Array[Double] = new Array[Double](array.length - 2)
        for (j <- 0 until features.length)
          features(j) = array(j + 2)
        LabeledPoint(array(0), Vectors.dense(features))
      }
    }
  })
  val trainRDD3: RDD[(Int, LabeledPoint)] = trainRDD1.mapPartitions(iter => {
    new Iterator[(Int, LabeledPoint)]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): (Int, LabeledPoint) = {
        val line: String = iter.next()
        val array: Array[Double] = line.split(splitChar).map(_.toDouble)
        val features: Array[Double] = new Array[Double](array.length - 2)
        for (j <- 0 until features.length)
          features(j) = array(j + 2)
        (array(1).toInt, LabeledPoint(array(0), Vectors.dense(features)))
      }
    }
  })



  val predictRDD1: RDD[String] = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/gender-predict-dataset").mapPartitions(iter => {
    new Iterator[String]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): String = {
        val line: String = iter.next()
        line.replace("(", "").replace(")", "")
      }
    }
  })
  val predictRDD2: RDD[LabeledPoint] = predictRDD1.mapPartitions(iter => {
    new Iterator[LabeledPoint]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): LabeledPoint = {
        val line: String = iter.next()
        val array: Array[Double] = line.split(splitChar).map(_.toDouble)
        val features: Array[Double] = new Array[Double](array.length - 2)
        for (j <- 0 until features.length)
          features(j) = array(j + 2)
        LabeledPoint(array(0), Vectors.dense(features))
      }
    }
  })
  val predictRDD3: RDD[(Int, LabeledPoint)] = predictRDD1.mapPartitions(iter => {
    new Iterator[(Int, LabeledPoint)]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): (Int, LabeledPoint) = {
        val line: String = iter.next()
        val array: Array[Double] = line.split(splitChar).map(_.toDouble)
        val features: Array[Double] = new Array[Double](array.length - 2)
        for (j <- 0 until features.length)
          features(j) = array(j + 2)
        (array(1).toInt, LabeledPoint(array(0), Vectors.dense(features)))
      }
    }
  })

  val allrdd = trainRDD3.union(predictRDD3)

  predictRDD2.filter(_.label == 1.0).count


  val rdd3: RDD[String] = sc.textFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/gender-model-data")
  val rdd4: RDD[String] = rdd3.mapPartitions(iter => {
    new Iterator[String]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): String = {
        val line: String = iter.next()
        line.replace("(", "").replace(")", "")
      }
    }
  })
  val rdd5 = rdd4.mapPartitions(iter => {
    new Iterator[LabeledPoint]() {
      override def hasNext: Boolean = iter.hasNext

      override def next(): LabeledPoint = {
        val line: String = iter.next()
        val array: Array[Double] = line.split(splitChar).map(_.toDouble)
        val features: Array[Double] = new Array[Double](array.length - 2)
        for (j <- 0 until features.length)
          features(j) = array(j + 2)
        LabeledPoint(array(0), Vectors.dense(features))
      }
    }
  })
  val rdds = rdd5.randomSplit(Array(0.8, 0.2), 1l)
  val train = rdds(0)
  val test = rdds(1)



  val alldataRDD: RDD[String] = trainRDD1.union(predictRDD1)
  alldataRDD.saveAsTextFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/gender-nomal-data-sex-uid-apps")



  // Run training algorithm to build the model
  val numIterations = 100
  val model = SVMWithSGD.train(predictRDD2, numIterations)

  // Clear the default threshold.
  model.clearThreshold()

  // Compute raw scores on the test set.
  val scoreAndLabels = trainRDD2.map { point =>
    val score = model.predict(point.features)
    (score, point.label)
  }


  val predictionAndLabels = allrdd.map { value =>
    val prediction = model.predict(value._2.features)
    (value._1, value._2.label, prediction)
  }
  predictionAndLabels.saveAsTextFile("hdfs://mz-hadoop-1.meizu.com:9000/user/hadoop/xuhong/gender-logistic-predict-result")


  // Get evaluation metrics.
  val metrics = new BinaryClassificationMetrics(scoreAndLabels)
  val auROC = metrics.areaUnderROC()

  println("Area under ROC = " + auROC)

  // Save and load model
  model.save(sc, "myModelPath")
  val sameModel = SVMModel.load(sc, "myModelPath")

}
