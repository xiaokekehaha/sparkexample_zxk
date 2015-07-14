package cn.chinahadoop.spark.ch09
import java.text.SimpleDateFormat
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import com.github.nscala_time.time.Imports._
import java.io.File
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashMap

import scala.collection.immutable
class RunRiskTest {

}
object RunRiskTest {

  def main(args: Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("RiskTest"))
    //D:\sparktestdata\risk
    val (stocksReturns, factorsReturns) = readStocksAndFactors("d:/sparktestdata/risk/")
    val dd=  factorsReturns(2)
     plotDistribution(factorsReturns(2))
     plotDistribution(factorsReturns(3))
    val numbers = Map("one" -> 1, "two" -> 2)
    
    //numbers.
  }
  
  def plotDistribution(data:Array[Double])={
   val min = data.min
   val max=data.max
  val test=  Range.Double
  val domain = Range.Double(min, max, (max - min) / 100)
  // val test=da
   var z = new Array[String](3)
   //  z.addString("s")
  
  }

  def readStocksAndFactors(prefix: String): (Seq[Array[Double]], Seq[Array[Double]]) = {

    val start = new DateTime(2009, 10, 23, 0, 0)
    val end = new DateTime(2014, 10, 23, 0, 0)

    val rawStocks = readHistories(new File(prefix + "stocks/")).filter(_.size > 260 * 5 + 10)

    val stocks = rawStocks.map(trimToRegion(_, start, end))
    val factorsPrefix = prefix + "factors/"

    val data = Array("", "").map {
      x =>
        new File(factorsPrefix + x)
    }.map(readInvestingDotComHistory)
    val factors1 = Array("crudeoil.tsv", "us30yeartreasurybonds.tsv").
      map(x => new File(factorsPrefix + x)).
      map(readInvestingDotComHistory)

    val factors2 = Array("SNP.csv", "NDX.csv").
      map(x => new File(factorsPrefix + x)).
      map(readYahooHistory)

    val factors = (factors1 ++ factors2).
      map(trimToRegion(_, start, end)).
      map(fillInHistory(_, start, end))
      
      val stockReturns = stocks.map(twoWeekReturns)
      val factorReturns = factors.map(twoWeekReturns)
    //val 
  (stockReturns, factorReturns)
  }
  
    def twoWeekReturns(history: Array[(DateTime, Double)]): Array[Double] = {
    history.sliding(10).map(window => window.last._2 - window.head._2).toArray
  }

  def fillInHistory(data: Array[(DateTime, Double)], data2: DateTime, data3: DateTime) = {

    val cul = data

    val filled = new ArrayBuffer[(DateTime, Double)]()

   // val test=filled.
    var curDate = data2
    
    while(data2<data3){
      
      if(cul.tail.nonEmpty&&cul.tail.head._1==curDate){
        
        // cul = cul.tail
      }
       filled += ((curDate, cul.head._2))

      curDate += 1.days
      // Skip weekends
      if (curDate.dayOfWeek().get > 5) curDate += 2.days
    }

     filled.toArray
  }
  def trimToRegion(history: Array[(DateTime, Double)], start: DateTime, end: DateTime): Array[(DateTime, Double)] = {

    var trimmed = history.dropWhile(_._1 < start).takeWhile(_._1 <= end)

    if (trimmed.head._1 != start) {

      trimmed = Array((start, trimmed.head._2)) ++ trimmed
    }
    if (trimmed.last._1 != end) {
      trimmed = trimmed ++ Array((end, trimmed.last._2))
    }
    trimmed

  }
  def readInvestingDotComHistory(file: File): Array[(DateTime, Double)] = {

    val formatdata = new SimpleDateFormat("MMM d, yyyy")
    val lines = Source.fromFile(file).getLines().toSeq

    lines.map {
      line =>
        val cols = line.split(",")
        val data = new DateTime(formatdata.parse(cols(0)))
        val value = cols(1).toDouble
        (data, value)
    }.reverse.toArray

  }

  def readHistories(file: File): Seq[Array[(DateTime, Double)]] = {

    val files = file.listFiles()
    files.flatMap {
      line =>

        try {
          Some(readYahooHistory(line))
        } catch {
          case e: Exception => None
        }

    }

  }

  def readYahooHistory(file: File): Array[(DateTime, Double)] = {
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val lines = Source.fromFile(file).getLines().toSeq
    lines.tail.map(line => {
      val cols = line.split(',')
      val date = new DateTime(format.parse(cols(0)))
      val value = cols(1).toDouble
      (date, value)
    }).reverse.toArray
  }

}