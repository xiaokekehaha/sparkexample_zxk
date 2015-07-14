/*package cn.chinahadoop.spark

import scala.Array.canBuildFrom
import scala.collection.mutable.ListBuffer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.serializer.KryoRegistrator
import com.esotericsoftware.kryo.Kryo


class Analysis {


  
}

object Analysis extends KryoRegistrator {
  
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[org.apache.spark.serializer.KryoSerializer])
    kryo.register(classOf[cn.chinahadoop.spark.Analysis])
  }

  def main(args: Array[String]) {

 
    val conf = new SparkConf()
    conf.setMaster("spark://192.168.7.127:7077")
      .setSparkHome("/home/yarn/spark/spark-0.9.0-incubating-bin-hadoop2")
      .setAppName("analysis")
  
      .set("spark.executor.memory", "60g")

      
      
System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
System.setProperty("spark.kryo.registrator", "mypackage.MyRegistrator")
System.setProperty("spark.storage.memoryFraction", "0.5")    

      val sc = new SparkContext(conf)


}



*/