/*package com.meizu




import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.io.Source



class ArgsOptsConf (arguments: Seq[String]) extends ScallopConf(arguments) {

    banner("""Usage: stream [OPTION]...
             |stream is a framework based on spark streaming for data streaming processing.
             |Options:
             |""".stripMargin)
//    val properties: Map[String, String] = propsLong[String](name = "hiveconf", keyName = "property", descr = "Use value for given property")
    val confPath: ScallopOption[String] = opt[String](required = true, name = "conf", argName = "properties-file", noshort = true, descr = "configuration file")
}

*//**
 * 车辆行驶记录
 * @param `cn` 底盘号
 * @param `st` 系统时间
 * @param `ct` 采集时间
 * @param `4013` 车辆打火状态 1启动状态
 * @param `0006` 速度(单位:公里/小时)
 * @param `0009` 发动机转速(单位:转/分)
 *//*
 case class VehHistoryTravel(val `cn`:String,val `st`:String,val `ct`:String,val `0002`:String,val `0003`:String,val `4013`:String,val `0006`:String,val `0009`:String)
*//**
 * 记录当前车的启动时间和行驶时长
 * @param cn 底盘号
 * @param ct 采集时间
 * @param proCode 省市代码
 * @param cityCode 地区
 * @param countyCode 县
 * @param totalTime 总时间
 * @param status 状态，0-已停止，删除历史数据，1-正在运行
 *//*
  case class TravelInfo(val cn:String,var ct:String,var proCode:String,var cityCode:String,var countyCode:String,var totalTime:Long, var status:Int)
*//**
 * @author ${user.name}
 *//*
object WeatherAlarm extends Logging{

    def getProperty(key: String, default: Option[String]): String = {
        if (key == null) {
            throw new IllegalArgumentException("invalid property key")
        }
        val value = System.getProperty(key)
        if (value == null) {
            default match {
                case Some(prop) => prop
                case None => throw new IllegalArgumentException("invalid property " + key)
            }
        } else {
            if (value.trim.isEmpty){
                throw new IllegalArgumentException("invalid property " + key)
            } else {
                value.trim
            }
        }
    }

    def createStreamingContext(checkpointDirectory: String): StreamingContext = {
        val appName  = getProperty("roilandstream.spark.appName", Some("stream-weather-alarm"))
        val streamingInterval = getProperty("roilandstream.spark.streaming.interval", None).toLong
        val brokers = getProperty("roilandstream.kafka.broker.list", None)
        val topics  = getProperty("roilandstream.kafka.topic", None)
        val db =getProperty("roilandstream.hive.db",Some("defalut"))
        val alarmTime = getProperty("roilandstream.weather.alarm.time",Some("1"))
        val weatherTyep = getProperty("roilandstream.weather.alarm.type",None)
        val sparkConf = if (!appName.trim.isEmpty) {
            new SparkConf().setAppName(appName.trim)
        } else {
            new SparkConf()
        }
        sparkConf.set("roilandstream.hive.db",db)
        sparkConf.set("roilandstream.weather.alarm.time",alarmTime)
        sparkConf.set("roilandstream.weather.alarm.type",weatherTyep)
        sparkConf.setMaster("local[3]")
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        val ssc = new StreamingContext(sparkConf, Seconds(streamingInterval))

        val topicsSet = topics.split(",").toSet
        val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
        val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
            ssc, kafkaParams, topicsSet)

        // Get the lines, split them into words, count the words and print
        val lines = messages.map(_._2)

        val travelStream = lines.map(line => {
            implicit val formats = DefaultFormats
            val json = parse(line)
            val t = json.extract[VehHistoryTravel]
            (t.`cn`,t)
        })

        val updateFunc = (values: Seq[VehHistoryTravel], state: Option[TravelInfo]) => {
            var prvState = state.orNull
            values.foreach(t => {
                //启动状态
                if("1".equals(t.`4013`)){
                   //计算行驶时长，当前时间减去开始采集时间
                    prvState match {
                        case prvState: TravelInfo => {
                            try {
                                val currentCt = t.`ct`
                                val prvCt = prvState.ct
                                import java.text.SimpleDateFormat
                                val sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss")
                                val d1 = sdf.parse(currentCt)
                                val d2 = sdf.parse(prvCt)
                                val interval = d1.getTime - d2.getTime
                                prvState.totalTime = interval
                                val addres = AddressUtil.getCarAddress(t.`0002`, t.`0003`)
                                prvState.proCode = addres(1)
                                prvState.cityCode = addres(3)
                                prvState.countyCode = addres(5)
                            } catch {
                                case e: Exception => logError("Parse date error", e)
                            }
                        }
                        case _ => {
                            val addres = AddressUtil.getCarAddress(t.`0002`, t.`0003`)
                            prvState = new TravelInfo(t.`cn`, t.`ct`, addres(1), addres(3), addres(5), 0L, 1)
                        }
                    }
                }else{
                    prvState match {
                        case prvState: TravelInfo => {
                            prvState.status = 0
                        }
                        case _ =>
                    }
                }
            })
            Some(prvState)
        }
        val travelTimeStream = travelStream.updateStateByKey(updateFunc)

        travelTimeStream.foreachRDD( rdd => {
            if(!rdd.isEmpty()){
                val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
                import sqlContext.implicits._
                val travleRdd = rdd.map(t => { t._2})
                val travelDF = travleRdd.toDF()
                travelDF.registerTempTable("travel_time")
                val db = rdd.sparkContext.getConf.get("roilandstream.hive.db")
                val alarmTime = rdd.sparkContext.getConf.get("roilandstream.weather.alarm.time")
                val weatherTyep = rdd.sparkContext.getConf.get("roilandstream.weather.alarm.type")
                //查询天气类型是制定类型，运行时长是指定阀值的整数倍
//                val weatherAlarm = sqlContext.sql("select t1.cn,t1.totalTime, t1.proCode,t1.cityCode,t1.countyCode  from travel_time t1 left outer join " +
//                  db + ".d_weather_info t2 on t1.countyCode = t2.COUNTY_CODE where t2.WEATHER_TYPE='"
//                  +weatherTyep+"' and t1.totalTime/(60*1000*"+alarmTime.toInt+") >= 1")
                val weatherAlarm = sqlContext.sql("select * from travel_time")

                weatherAlarm.show()
            }
        })
        ssc.checkpoint(checkpointDirectory)
        ssc
    }

    def setEnv(path: String) : Unit = {
        val propertiesMatcher = """^\s*([^\s]+)\s+(.*)""".r
        val lines = Source.fromFile(path).getLines().toList
        val properties = for (line <- lines.map(_.trim).filter(!_.startsWith("//"))) yield {
            line match {
                case propertiesMatcher(key, value) => (key.trim, value.trim)
                case _ => (null, null)
            }
        }
        for (entity <- properties.filter(_._1 != null)) {
            System.setProperty(entity._1, entity._2)
        }
    }

    def main(args : Array[String]) {
        val optsConf = if (args.length != 0) {
            new ArgsOptsConf(args)
        } else {
            new ArgsOptsConf(List("--help"))
        }
        val confPath = optsConf.confPath.get.get
        setEnv(confPath)
        val checkpointDirectory = getProperty("roilandstream.spark.streaming.checkpointDir", None)
        val ssc = StreamingContext.getOrCreate(checkpointDirectory, () => createStreamingContext(checkpointDirectory))
        ssc.start()
        ssc.awaitTermination()
        sys.addShutdownHook({ssc.stop(true,true)}).start()
    }



    *//** Lazily instantiated singleton instance of SQLContext *//*
    object SQLContextSingleton {

        @transient private var instance: SQLContext = _

        def getInstance(sparkContext: SparkContext): SQLContext = {
            if (instance == null) {
                instance = new SQLContext(sparkContext)
//                instance.udf.register("getLocation",func = (longitude: String, latitude: String) => {
//                    AddressUtil.getCarAddress(longitude,latitude)
//                })
            }
            instance
        }
    }
}
*/