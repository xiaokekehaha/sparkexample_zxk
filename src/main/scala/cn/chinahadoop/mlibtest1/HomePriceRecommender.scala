package cn.chinahadoop.mlibtest1

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{ Path, FileSystem }
import org.apache.spark.mllib.feature.{ StandardScalerModel, StandardScaler }
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{ SparkContext, SparkConf }
import sun.security.util.Length
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object HomePriceRecommender extends Serializable {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Home Price Recommender").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val homeData = sc.textFile("file:///d:/sparktestdata/data.txt")

    val data = sc.textFile("/Users/chenchao/workspace/chinahadoop/data/ridge-data/lpsa.data")
    //sc.hadoopConfiguration.set(name, value)
    homeData.cache();

    //val parsed = homeData.map(line => line.split("\t")).map(x=>x._1)

    val dataAppNum = homeData.map(line => line.split("\t")(3)).distinct.toArray

    for (i <- 0 to (dataAppNum.length - 1)) {

      println("==" + dataAppNum(i))

    }

    val dataAppfilter = homeData.filter(_.split("\t").length == 4).map {
      line =>
        val dataApp = line.split('\t')
        val id = dataApp(0) + "," + dataApp(1)
        val appid = dataApp(3)

        (id, appid)
    }.reduceByKey(
      (x, y) => x + "," + y)

    // dataAppfilter.foreach(println)

    val MarkApp = dataAppfilter.map { data =>

      val appnum = data._2
      val appProperty = new Array[Int](dataAppNum.length)

      for (i <- 0 to (appProperty.length - 1)) {

        val app: Array[String] = appnum.split(",")

        for (j <- 0 to (app.length - 1)) {

          if (dataAppNum(i) == app(j)) {

            appProperty(i) = 1
          }

        }

      }

      (data._1, appProperty.mkString(","))
    }

    // MarkApp.foreach(println)

    val labelData2 = MarkApp.map { data =>

      val data1 = data._1.split(",")
      LabeledPoint(data1(0).toDouble, Vectors.dense(data._2.trim().split(',').map(java.lang.Double.parseDouble)))
    }.cache()

    //构建模型
    val numIterations = 20
    val model = LinearRegressionWithSGD.train(labelData2, numIterations)

    //预测
    val valuesAndPreds = labelData2.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    //计算MSE
    val MSE = valuesAndPreds.map { case (v, p) => math.pow((v - p), 2) }.reduce(_ + _) / valuesAndPreds.count
    println("training Mean Squared Error = " + MSE)

  }

}

    
 
    
    
    
    
    
    
    
