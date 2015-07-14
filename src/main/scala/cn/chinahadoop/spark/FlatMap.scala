package cn.chinahadoop.spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
object FlatMap {
   def main(args: Array[String]) {
    
    val sc = new SparkContext("local", "FlatMap Test") 
	val data = Array[(String, Int)](("A", 1), ("B", 2),
		    						 ("B", 3), ("C", 4),
		    						 ("C", 5), ("C", 6),("A", 6)
		    						)    							
	val pairs = sc.makeRDD(data, 3)
	
	val mystr="this is one dog"
    // mystr.map(split(" ")).size
	
	val tt=pairs.countApproxDistinctByKey(0.05)
	tt.foreach(println)
	
	//visitors.distinct().mapValues(_ => 1).reduceByKey(_ + _)
	//visitors.groupByKey().map { 
//  case (webpage, visitor_iterable)
 // => (webpage, visitor_iterable.toArray.distinct.length)
//}
	pairs.distinct().map(_._1).countByValue()
	
	val result = pairs.flatMap(T => (T._1 + T._2))
	
	println("result 1:"+result.take(1))
	
	val m = Map[String, Int]("a" -> 1, "b" -> 2, "c" -> 3)
   //   m.foreach((key: String, value: Int) => println(">>> key=" + key + ", value=" + value))
	m.foreach((e: (String, Int)) => println(e._1 + "=" + e._2))
	//result.foreach((i: String,j: Int) => i +"")
	m.foreach{ case (key: String, value: Int) => println(">>> key=" + key + ", value=" + value)}
	//result.foreach(println)
    
    val array2=Iterator(Array(1.1, 2.2, 3.3, 4.4), Array(2.2, 4.4, 6.6, 8.8))
    
    //array2.foreach(println)
    val data_test=array2.map(_.apply(0)).toVector.sum
    println("+++"+data_test)
//array2: Iterator[Array[Double]] = non-empty iterator

 //println(array2.map(_.apply(0)))
 
    val array3=array2
    
    array3.foreach(println)
   println("+++"+array2.map(_.apply(0)).toVector.sum)
//得到了Vector(1.1, 2.2)
//比如我想获得这个Vector的内容
val vect=array2.map(_.apply(0))
//vect: Vector[Double] = Vector()
//vect(0)
//println(array2.map(_.apply(0)).toVector)
//vect.foreach(println)
println("====="+data_test)

println(array2.map(_.apply(0)).toVector)//这样得到的结果是
//0.0

    
/*val links = sc.objectFile[(String, Seq[String])]("links").partitionBy(new HashPartitioner(100)).persist()

// Initialize each page's rank to 1.0; since we use mapValues, the resulting RDD
// will have the same partitioner as links
var ranks = links.mapValues(_ => 1.0)

// Run 10 iterations of PageRank
for (i <- 0 until 10) {
  val contributions = links.join(ranks).flatMap {
    case (pageId, (links, rank)) =>
      links.map(dest => (dest, rank / links.size))
  }
  ranks = contributions.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
}
ranks.saveAsTextFile("ranks")
  

val userData = sc.sequenceFile[UserID, UserInfo]("hdfs://...").persist()

// Function called periodically to process a log file of events in the past 5 minutes;
// we assume that this is a SequenceFile containing (UserID, LinkInfo) pairs.
def processNewLogs(logFileName: String) {
  val events = sc.sequenceFile[UserID, LinkInfo](logFileName)
  val joined = userData.join(events)// RDD of (UserID, (UserInfo, LinkInfo)) pairs
  val offTopicVisits = joined.filter {
    case (userId, (userInfo, linkInfo)) => // Expand the tuple into its components
      !userInfo.topics.contains(linkInfo.topic)
  }.count()
  println("Number of visits to non-subscribed topics: " + offTopicVisits)
}
*/
  }
   
   
   
   
}