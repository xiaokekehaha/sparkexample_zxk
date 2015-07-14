package cn.chinahadoop.spark.ch08

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
class BechmMl {

}

object BechMl {
  def main(args: Array[String]) {
    val d_train_0 = csv_to_sparse_labpoint("spark-train-1m.csv")
    val d_test = csv_to_sparse_labpoint("spark-test-1m.csv")
    d_train_0.partitions.size
    val d_train = d_train_0.repartition(32)
    d_train.partitions.size
    d_train.cache()
    d_test.cache()
    d_test.count()
    d_train.count()
val now = System.nanoTime
val model = new LogisticRegressionWithLBFGS().setIntercept(true).run(d_train)
( System.nanoTime - now )/1e9

// WARN BLAS: Failed to load implementation from: com.github.fommil.netlib.NativeSystemBLAS
// WARN BLAS: Failed to load implementation from: com.github.fommil.netlib.NativeRefBLAS
// 5sec
//val model0 = new LogisticRegressionWithLBFGS()
//model0.optimizer.setConvergenceTol(0.0001)
//model0.optimizer.setNumIterations(100)
//val model = model0.setIntercept(true).run(d_train)
model.clearThreshold()  
val scoreAndLabels = d_test.map { point =>
  val score = model.predict(point.features)
  (score, point.label)
}

val metrics = new BinaryClassificationMetrics(scoreAndLabels)
metrics.areaUnderROC()
// 0.70362

// setConvergenceTol(0.00001)   0.70348
// setConvergenceTol(0.0001)    0.70362  (default)
// setConvergenceTol(0.001)    0.70401
// setConvergenceTol(0.01)    0.70284
// setNumIterations(10)  0.70209
// setNumIterations(100) 0.70362 (default)
// setNumIterations(1000)  0.70362





  }
  def csv_to_sparse_labpoint(fname: String): org.apache.spark.rdd.RDD[LabeledPoint] = {

    val sc = new SparkContext(new SparkConf().setMaster("local[2]"))
    val rdd = sc.textFile(fname).map({ line =>
      val vv = line.split(',').map(_.toDouble)
      val label = vv(0)
      val X = vv.slice(1, vv.size)
      val n = X.filter(_ != 0).length
      var X_ids = Array.fill(n) { 0 }
      var X_vals = Array.fill(n) { 0.0 }
      var kk = 0
      for (k <- 0 to X.length - 1) {
        if (X(k) != 0) {
          X_ids(kk) = k
          X_vals(kk) = X(k)
          kk = kk + 1
        }
      }
      val features = Vectors.sparse(X.length, X_ids, X_vals)
      LabeledPoint(label, features)
    })
    return rdd
  }

}