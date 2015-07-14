package cn.chinahadoop.log
import org.apache.spark.{ SparkContext, SparkConf }
import scala.collection.mutable.ListBuffer
import org.apache.spark.SparkContext._


class SparklogTest {

}

object SparklogTest {

  val sc = new SparkContext("local", "log Test")

  val log = sc.textFile("accesslog.small")
  import cn.chinahadoop.log._
  val p = new AccessLogParser
  def main(args: Array[String]) {

    log.filter(line => getStatusCode(p.parseRecord(line)) == "404").map(getRequest(_)).count
    log.filter(line => getStatusCode(p.parseRecord(line)) == "404").count
    val recs = log.filter(line => getStatusCode(p.parseRecord(line)) == "404").map(getRequest(_))
    val distinctRecs = log.filter(line => getStatusCode(p.parseRecord(line)) == "404").map(getRequest(_)).distinct
    distinctRecs.foreach(println)

    val distinctRecs2 = log.filter(line => getStatusCode(p.parseRecord(line)) == "404")
      .map(getRequest(_))
      .collect { case Some(requestField) => requestField }
      .map(extractUriFromRequest(_))
      .distinct
    distinctRecs2.count
    distinctRecs2.foreach(println)
  }

  val uris = log.map(p.parseRecordReturningNullObjectOnFailure(_).request)
    .filter(_ != "")
    .map(_.split(" ")(1))

  val uris2 = log.map(line => p.parseRecordReturningNullObjectOnFailure(line).request)
    .filter(request => request != "")
    .map(request => request.split(" ")(1)) // a request looks like "GET /foo HTTP/1.1"
  // works: use the previous example to get to a series of "(URI, COUNT)" pairs; (MapReduce like)
  val uriCount = log.map(p.parseRecordReturningNullObjectOnFailure(_).request)
    .filter(request => request != "") // filter out records that wouldn't parse properly
    .map(_.split(" ")(1)) // get the uri field
    .map(uri => (uri, 1)) // create a tuple for each record
    .reduceByKey((a, b) => a + b) // reduce to get this for each record: (/java/java_oo/up.png,2)
    .collect
  import scala.collection.immutable.ListMap
  val uriHitCount = ListMap(uriCount.toSeq.sortWith(_._2 > _._2): _*)

   val data = """
124.30.9.161 - - [21/Jul/2009:02:48:11 -0700] "GET /java/edu/pj/pj010004/pj010004.shtml HTTP/1.1" 200 16731 "http://www.google.co.in/search?hl=en&client=firefox-a&rlz=1R1GGGL_en___IN337&hs=F0W&q=reading+data+from+file+in+java&btnG=Search&meta=&aq=0&oq=reading+data+" "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 GTB5"
89.166.165.223 - - [21/Jul/2009:02:48:12 -0700] "GET /favicon.ico HTTP/1.1" 404 970 "-" "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11"
94.102.63.11 - - [21/Jul/2009:02:48:13 -0700] "GET / HTTP/1.1" 200 18209 "http://www.developer.com/net/vb/article.php/3683331" "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)"
124.30.7.162 - - [21/Jul/2009:02:48:13 -0700] "GET /images/tline3.gif HTTP/1.1" 200 79 "http://www.devdaily.com/java/edu/pj/pj010004/pj010004.shtml" "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 GTB5"
122.165.54.17 - - [21/Jul/2009:02:48:12 -0700] "GET /java/java_oo/ HTTP/1.1" 200 32579 "http://www.google.co.in/search?hl=en&q=OO+with+java+standalone+example&btnG=Search&meta=&aq=f&oq=" "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7"
217.32.108.226 - - [21/Jul/2009:02:48:13 -0700] "GET /blog/post/perl/checking-testing-perl-module-in-inc-include-path/ HTTP/1.1" 200 18417 "http://www.devdaily.com/blog/post/perl/perl-error-cant-locate-module-in-inc/" "Mozilla/5.0 (X11; U; SunOS i86pc; en-US; rv:1.7) Gecko/20070606"
122.165.54.17 - - [21/Jul/2009:02:48:15 -0700] "GET /java/java_oo/java_oo.css HTTP/1.1" 200 1235 "http://www.devdaily.com/java/java_oo/" "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7"
66.249.70.10 - - [23/Feb/2014:03:21:59 -0700] "GET /blog/post/java/how-load-multiple-spring-context-files-standalone/ HTTP/1.0" 301 - "-" "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
 
""".split("\n").filter(_ != "") 
  
      val records = data
      val parser = new AccessLogParser
      val rec = parser.parseRecord(records(1))
      
      
      
  def getStatusCode(line: Option[AccessLogRecord]) = {
    line match {
      case Some(l) => l.httpStatusCode
      case None => "0"
    }
  }

  // get the `request` field from an access log record
  def getRequest(rawAccessLogString: String): Option[String] = {
    val accessLogRecordOption = p.parseRecord(rawAccessLogString)
    accessLogRecordOption match {
      case Some(rec) => Some(rec.request)
      case None => None
    }
  }

  def extractUriFromRequest(requestField: String) = requestField.split(" ")(1)

}