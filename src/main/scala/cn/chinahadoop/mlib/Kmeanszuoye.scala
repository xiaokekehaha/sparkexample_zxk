
package cn.chinahadoop.mlib
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

object Kmeanszuoye {
  def main(args: Array[String]) {

    // 设置运行环境
    val conf = new SparkConf().setAppName("Kmeans").setMaster("local[4]")
    val sc = new SparkContext(conf)

    //装载数据集
    val data = sc.textFile("file:///d:/riyongruanjian/Cholesterol.csv", 1)
    val parsedData = data.map(s => Vectors.dense(s.split(',').map(_.toDouble)))

    //将数据集聚类，5个类，20次迭代，形成数据模型
    val numClusters = 5
    val numIterations = 20
    val model = KMeans.train(parsedData, numClusters, numIterations)

    //数据模型的中心点
    println("Cluster centers:")
    for (c <- model.clusterCenters) {
      println("  " + c.toString)
    }


    sc.stop()
  }
}
