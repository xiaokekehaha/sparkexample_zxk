package cn.chinahadoop.dataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.{ SparkContext, SparkConf }

import org.apache.spark.SparkContext._
import org.apache.spark.sql.functions._

class DataFrameTest {

}

private case class Cust(id: Integer, name: String,
  sales: Double, discount: Double, state: String)

object DataFrameTest {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("dataframe").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    //  val df = sqlContext.jsonFile("examples/src/main/resources/people.json")

    import sqlContext.implicits._

    val custs = Seq(
      Cust(1, "Widget Co", 120000.00, 0.00, "AZ"),
      Cust(2, "Acme Widgets", 410500.00, 500.00, "CA"),
      Cust(3, "Widgetry", 410500.00, 200.00, "CA"),
      Cust(4, "Widgets R Us", 410500.00, 0.0, "CA"),
      Cust(5, "Ye Olde Widgete", 500.00, 0.0, "MA"))
    val customerDF = sc.parallelize(custs, 4).toDF()

    val myFunc = udf { (x: Double) => x + 1 }
    val colNames = customerDF.columns
    val cols = colNames.map(cName => customerDF.col(cName))
    val theColumn = customerDF("discount")

    val mappedCols = cols.map(c =>
      if (c.toString() == theColumn.toString()) myFunc(c).as("transformed") else c)
    val newDF = customerDF.select(mappedCols: _*)
    newDF.show()
    
    
  }

}