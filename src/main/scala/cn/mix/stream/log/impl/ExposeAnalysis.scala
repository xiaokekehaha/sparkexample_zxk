package com.asiainfo.mix.log.impl

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream.DStream
import cn.mix.stream.log.streaming_log.LogTools
import cn.mix.stream.log.streaming_log.StreamAction
import cn.mix.stream.log.xml.XmlProperiesAnalysis
import cn.mix.stream.log.streaming_log.DimensionEditor

/**
 * @author surq
 * @since 2014.07.15
 * 曝光日志 流处理
 */
class ExposeAnalysis extends StreamAction with Serializable {

  /**
   * @param inputStream:log流数据<br>
   */
  override def run(logtype: String, inputStream: DStream[Array[(String, String)]], logSteps: Int): DStream[Array[(String, String)]] = {
    printInfo(this.getClass(), "ExposeAnalysis is running!")

    val logPropertiesMaps = XmlProperiesAnalysis.getLogStructMap
    val logPropertiesMap = logPropertiesMaps(logtype)

    // log数据主key
    val keyItems = logPropertiesMap("rowKey").split(",")
    // rowkey 连接符
    val separator = "asiainfoMixSeparator"

    inputStream.map(record => {
      val itemMap = record.toMap
      val keyMap = (for { key <- keyItems } yield (key, itemMap(key))).toMap
      (DimensionEditor.getUnionKey("expose", keyMap, logSteps, separator), record)
    }).groupByKey.map(f => {

      val count = f._2.size
      val sumCost = for { f <- f._2; val map = f.toMap } yield (map("cost").toLong)

      // 创建db表结构并初始化
      var dbrecord = Map[String, String]()
      // 流计算
      dbrecord += (("bid_success_cnt") -> count.toString)
      dbrecord += (("expose_cnt") -> count.toString)
      dbrecord += (("cost") -> (sumCost.sum).toString)

      // rowKey: 多维度联合主键
      dbrecord += (("rowKey", f._1))
      dbrecord.toArray
    })
  }
}