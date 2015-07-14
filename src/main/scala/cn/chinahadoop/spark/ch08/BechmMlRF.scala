package cn.chinahadoop.spark.ch08
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
class BechmMlRF {

}
object BechmMlRF {

  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setMaster(""))
    
    /*
     *               val d_train_0 = csv_to_sparse_labpoint("/zhouxiaoke/milsongs-cls-train.csv",sc)
       val d_test = csv_to_sparse_labpoint("/zhouxiaoke/milsongs-cls-test.csv",sc)
     */
    val d_train_0 = csv_to_sparse_labpoint("spark-train-10m.csv", sc)
    val d_test = csv_to_sparse_labpoint("spark-test-10m.csv", sc)

    d_train_0.partitions.size
    val d_train = d_train_0.repartition(32)
    d_train.partitions.size

    d_train.cache()
    d_test.cache()

    d_test.count()
    d_train.count()

    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 20
    val featureSubsetStrategy = "sqrt"
    val impurity = "entropy"
    val maxDepth = 5
    val maxBins = 10

    val now = System.nanoTime
    val model = RandomForest.trainClassifier(d_train, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    (System.nanoTime - now) / 1e9

    val scoreAndLabels = d_test.map { point =>
      //val score = model.predict(point.features)  // does not work as it returns 0/1
      val score = model.trees.map(tree => tree.predict(point.features)).filter(_ > 0).size.toDouble / model.numTrees
      (score, point.label)
    }

    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    metrics.areaUnderROC()

  }
  def csv_to_sparse_labpoint(fname: String, sc: SparkContext): org.apache.spark.rdd.RDD[LabeledPoint] = {

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