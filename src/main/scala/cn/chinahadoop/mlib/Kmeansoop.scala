
package cn.chinahadoop.mlib
import org.apache.log4j.{ Level, Logger }
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
object Kmeansoop {
  def main(args: Array[String]) {
    //屏蔽不必要的日志显示在终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // 设置运行环境
    val conf = new SparkConf().setAppName("Kmeans").setMaster("local[4]")
    val sc = new SparkContext(conf)

    //装载数据集
    /*    val data = sc.textFile("/tem/Cholesterol.csv", 1)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble)))*/

    //加载数据
    val data = sc.textFile("file:///d:/riyongruanjian/ratings.txt")
    //data中每条数据经过map的split后会是一个数组，模式匹配后，会new一个Rating对象
    val ratings = data.map(_.split("::") match {
      case Array(user, item, rate, ts) =>
        Rating(user.toInt, item.toInt, rate.toDouble)
    })

    ratings take 2
    val rank = 10
    val numIterations = 30
    val model = ALS.train(ratings, rank, numIterations, 0.01)

    val usersProducts = ratings.map {
      case Rating(user, product, rate) =>
        (user, product)
    }

    //预测后的用户，电影，评分  
    val predictions =
      model.predict(usersProducts).map {
        case Rating(user, product, rate) =>

          ((user, product), rate)
      }

    //原始{(用户，电影)，评分} join  预测后的{(用户，电影)，评分}  
    /*val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>((user, product), rate) }.join(predictions)*/
    usersProducts.collect take 3
    predictions.collect take 3

    println("========" + usersProducts.collect take 3)
    println("=============" + predictions.collect take 3)

    // model.predict(1,2)

  }
}
