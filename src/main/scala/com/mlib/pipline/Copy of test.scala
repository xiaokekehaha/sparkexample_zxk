package com.mlib.pipline

import org.apache.spark.SparkContext._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.mllib.linalg.{ Vector, Vectors }
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{ Row, SQLContext }

class test2 {

}
object test2 extends App {
  val conf = new SparkConf().setAppName("dataframe").setMaster("local[4]")
  val sc = new SparkContext(conf)

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  import sqlContext.implicits._

  val training = sc.parallelize(Seq(
    LabeledPoint(1.0, Vectors.dense(0.0, 1.1, 0.1)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.0, -1.0)),
    LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
    LabeledPoint(1.0, Vectors.dense(0.0, 1.2, -0.5))))

  val lr = new LogisticRegression()

  lr.setMaxIter(10).setRegParam(0.01)
  val model1 = lr.fit(training.toDF)

  println("Model 1 was fit using parameters: " + model1.fittingParamMap)

  val paramMap = ParamMap(lr.maxIter -> 20)
  paramMap.put(lr.maxIter, 30) // Specify 1 Param.  This overwrites the original maxIter.
  paramMap.put(lr.regParam -> 0.1, lr.threshold -> 0.55) // Specify multiple Params.

  // One can also combine ParamMaps.
  val paramMap2 = ParamMap(lr.probabilityCol -> "myProbability") // Change output column name
  val paramMapCombined = paramMap ++ paramMap2

  // Now learn a new model using the paramMapCombined parameters.
  // paramMapCombined overrides all parameters set earlier via lr.set* methods.
  val model2 = lr.fit(training.toDF, paramMapCombined)
  println("Model 2 was fit using parameters: " + model2.fittingParamMap)

  // Prepare test data.
  val test = sc.parallelize(Seq(
    LabeledPoint(1.0, Vectors.dense(-1.0, 1.5, 1.3)),
    LabeledPoint(0.0, Vectors.dense(3.0, 2.0, -0.1)),
    LabeledPoint(1.0, Vectors.dense(0.0, 2.2, -1.5))))

  // Make predictions on test data using the Transformer.transform() method.
  // LogisticRegression.transform will only use the 'features' column.
  // Note that model2.transform() outputs a 'myProbability' column instead of the usual
  // 'probability' column since we renamed the lr.probabilityCol parameter previously.
  model2.transform(test.toDF).select("features", "label", "myProbability", "prediction").show()
  model2.transform(test.toDF).select("features", "label", "myProbability", "prediction").foreach {
      case Row(features: Vector, label: Double, prob: Vector, prediction: Double) =>
        println("($features, $label) -> prob=$prob, prediction=$prediction")}

  sc.stop()

}
