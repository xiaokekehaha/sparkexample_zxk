package api.examples

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.{SQLContext, SchemaRDD}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SaveMode
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.conf.Configuration
import org.apache.phoenix.spark._
object Collect {
  def main (args: Array[String]) {
    val conf = new SparkConf().setAppName("HiveQL")
    val sc = new SparkContext(conf)

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

// this is used to implicitly convert an RDD to a DataFrame.
  // import sqlContext.implicits._
  // val df = sqlContext.sql("create table xiaoke_test0528 as select count(*) from test_zhouxiaoke")
  // val df = sqlContext.sql(" select count(*) from default.test_zhouxiaoke")
 val configuration = new Configuration()
// Load the columns 'ID' and 'COL1' from TABLE1 as a DataFrame
val df22 = sqlContext.phoenixTableAsDataFrame(
  "TABLE1", Array("ID", "COL1"), conf = configuration
)

df22.show

    
   val df = sqlContext.load("org.apache.phoenix.spark", Map("table" -> "INPUT_TABLE",
  "zkUrl" -> ""))

// Save to OUTPUT_TABLE
df.save("org.apache.phoenix.spark", SaveMode.Overwrite, Map("table" -> "OUTPUT_TABLE", 
  "zkUrl" -> ""))
   
//val df2=sqlContext.sql("insert overwrite table xiaoke_test0528 select count(*) from default.test_zhouxiaoke")
   sc.stop
  }
}
