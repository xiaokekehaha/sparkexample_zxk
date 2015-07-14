
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

class GraphxTest {

}

object GraphxTest extends App {

  val conf = new SparkConf()

  val sc = new SparkContext("local", "FlatMap Test")

  /*
 * 准备数据
 */
  val vertexArray = Array(
    (1L, ("Alice", 28)),
    (2L, ("Bob", 27)),
    (3L, ("Charlie", 65)),
    (4L, ("David", 42)),
    (5L, ("Ed", 55)),
    (6L, ("Fran", 50)))
  val edgeArray = Array(
    Edge(2L, 1L, 7),
    Edge(2L, 4L, 2),
    Edge(3L, 2L, 4),
    Edge(3L, 6L, 3),
    Edge(4L, 1L, 1),
    Edge(5L, 2L, 2),
    Edge(5L, 3L, 8),
    Edge(5L, 6L, 3))

  val vertexRDD: RDD[(Long, (String, Int))] = sc.parallelize(vertexArray)
  val edgeRDD: RDD[Edge[Int]] = sc.parallelize(edgeArray)
  val graph: Graph[(String, Int), Int] = Graph(vertexRDD, edgeRDD)

  // Solution 1
  graph.vertices.filter { case (id, (name, age)) => age > 30 }.collect.foreach {
    case (id, (name, age)) => println(s"$name is $age")
  }

  // Solution 2
  graph.vertices.filter(v => v._2._2 > 30).collect.foreach(v => println(s"${v._2._1} is ${v._2._2}"))

  // Solution 3
  for ((id, (name, age)) <- graph.vertices.filter { case (id, (name, age)) => age > 30 }.collect) {
    println(s"$name is $age")

    /*
   * Bob likes Alice
Bob likes David
Charlie likes Bob
Charlie likes Fran
David likes Alice
Ed likes Bob
Ed likes Charlie
Ed likes Fran
   */

    for (triplet <- graph.triplets.collect) {

      println("结果跑是、、、、、、、、、、、、、、、、、" + triplet.toString)
      println(s"${triplet.srcAttr._1} likes ${triplet.dstAttr._1}")
    }
  }
  /*
   * find the lovers.
   */
  for (triplet <- graph.triplets.filter(t => t.attr > 5).collect) {
    println(s"${triplet.srcAttr._1} loves ${triplet.dstAttr._1}")
  }

  // Define a class to more clearly model the user property
  case class User(name: String, age: Int, inDeg: Int, outDeg: Int)
  // Create a user Graph
  val initialUserGraph: Graph[User, Int] = graph.mapVertices { case (id, (name, age)) => User(name, age, 0, 0) }

  // Fill in the degree information
  val userGraph = initialUserGraph.outerJoinVertices(initialUserGraph.inDegrees) {
    case (id, u, inDegOpt) => User(u.name, u.age, inDegOpt.getOrElse(0), u.outDeg)
  }.outerJoinVertices(initialUserGraph.outDegrees) {
    case (id, u, outDegOpt) => User(u.name, u.age, u.inDeg, outDegOpt.getOrElse(0))
  }

  for ((id, property) <- userGraph.vertices.collect) {
    println(s"User $id is called ${property.name} and is liked by ${property.inDeg} people.")
  }

  userGraph.vertices.filter {
    case (id, u) => u.inDeg == u.outDeg
  }.collect.foreach {
    case (id, property) => println(property.name)
  }

  // Find the oldest follower for each user
  val oldestFollower: VertexRDD[(String, Int)] = userGraph.mapReduceTriplets[(String, Int)](
    // For each edge send a message to the destination vertex with the attribute of the source vertex
    edge => Iterator((edge.dstId, (edge.srcAttr.name, edge.srcAttr.age))),
    // To combine messages take the message for the older follower
    (a, b) => if (a._2 > b._2) a else b)

  userGraph.vertices.leftJoin(oldestFollower) { (id, user, optOldestFollower) =>
    optOldestFollower match {
      case None => s"${user.name} does not have any followers."
      case Some((name, age)) => s"${name} is the oldest follower of ${user.name}."
    }
  }.collect.foreach { case (id, str) => println(str) } 

  val averageAge: VertexRDD[Double] = userGraph.mapReduceTriplets[(Int, Double)](
    // map function returns a tuple of (1, Age)
    edge => Iterator((edge.dstId, (1, edge.srcAttr.age.toDouble))),
    // reduce function combines (sumOfFollowers, sumOfAge)
    (a, b) => ((a._1 + b._1), (a._2 + b._2))).mapValues((id, p) => p._2 / p._1)

  // Display the results
  userGraph.vertices.leftJoin(averageAge) { (id, user, optAverageAge) =>
    optAverageAge match {
      case None => s"${user.name} does not have any followers."
      case Some(avgAge) => s"The average age of ${user.name}\'s followers is $avgAge."
    }
  }.collect.foreach { case (id, str) => println(str) }

  val olderGraph = userGraph.subgraph(vpred = (id, user) => user.age >= 30)
  // compute the connected components
  val cc = olderGraph.connectedComponents

  // display the component id of each user:
  olderGraph.vertices.leftJoin(cc.vertices) {
    case (id, user, comp) => s"${user.name} is in component ${comp.get}"
  }.collect.foreach { case (id, str) => println(str) }
}