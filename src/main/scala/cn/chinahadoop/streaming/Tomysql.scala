package cn.chinahadoop.streaming


import java.sql.{PreparedStatement, Connection, DriverManager}
import java.util.concurrent.atomic.AtomicInteger

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._


//No need to call Class.forName("com.mysql.jdbc.Driver") to register Driver?

object Tomysql {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("NetCatWordCount")
    conf.setMaster("local[3]")
    val ssc = new StreamingContext(conf, Seconds(5))
    //This <code>ds</code>tream object represents the stream of data that will be received from the data
    //server. Each record in this DStream is a line of text
    //The DStream is a collection of RDD, which makes the method foreachRDD reasonable
    val dstream = ssc.socketTextStream("192.168.26.140", 9999)
    dstream.foreachRDD(rdd => {
      //embedded function
      def func(records: Iterator[String]) {
        var conn: Connection = null
        var stmt: PreparedStatement = null
        try {
          val url = "jdbc:mysql://192.168.26.140:3306/person";
          val user = "root";
          val password = ""
          conn = DriverManager.getConnection(url, user, password)
          records.flatMap(_.split(" ")).foreach(word => {
            val sql = "insert into TBL_WORDS(word) values (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, word)
            stmt.executeUpdate();
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          if (stmt != null) {
            stmt.close()
          }
          if (conn != null) {
            conn.close()
          }
        }
      }

      val repartitionedRDD = rdd.repartition(3)
      repartitionedRDD.foreachPartition(func)
    })
    ssc.start()
    ssc.awaitTermination()
  }
}
