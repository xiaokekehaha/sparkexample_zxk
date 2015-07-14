package com.asiainfo.mix.log.impl

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream.DStream
import cn.mix.stream.log.streaming_log.LogTools
import cn.mix.stream.log.streaming_log.StreamAction
import cn.mix.stream.log.xml.XmlProperiesAnalysis
import cn.mix.stream.log.streaming_log.DimensionEditor
/**
 * @author surq
 * @since 2014.12.24
 * 转换日志 流处理
 */
class ChangeAnalysis extends StreamAction with Serializable {

  val ox003: Char = 3

  /**
   * @param inputStream:log流数据<br>
   */
  override def run(logtype: String, inputStream: DStream[Array[(String, String)]], logSteps: Int): DStream[Array[(String, String)]] = {
    printInfo(this.getClass(), "ChangeAnalysis is running!")

    val logPropertiesMaps = XmlProperiesAnalysis.getLogStructMap
    val logPropertiesMap = logPropertiesMaps(logtype)

    // log数据主key
    val keyItems = logPropertiesMap("rowKey").split(",")
    // rowkey 连接符
    val separator = "asiainfoMixSeparator"

    inputStream.filter(record => {
      val itemMap = record.toMap
      if (itemMap("order").trim.isEmpty) false else {
        // 第3个字段[MIX_UID]时间比第9个字段[order]中时间不大于5分钟的日志条数
        // （time_domain_sizeid_areaid_slotid_cid_oid_adid）
        val idList = LogTools.splitArray(itemMap("order"), ox003.toString, 10)
        val time = if (idList(0).trim == "") 0 else idList(0).toLong
        val logtime = itemMap("logtime").toLong - (7 * 24 * 3600 * 1000)
        if (logtime <= time) true else false
      }
    }).map(record => {
      val itemMap = record.toMap
      (itemMap("order") + itemMap("MIX_UID"), record)
    }).groupByKey.map(f => {
      val record = (f._2).head
      val itemMap = record.toMap
      val keyMap = (for { key <- keyItems } yield (key, itemMap(key))).toMap
      val rowKey = DimensionEditor.getUnionKey("change", keyMap, logSteps, separator)
      // 创建db表结构并初始化
      var dbrecord = Map[String, String]()
      // 流计算
      dbrecord += (("trans_cnt") -> "1")
      // rowKey: 多维度联合主键
      dbrecord += (("rowKey", rowKey))
      dbrecord.toArray
    })
  }
}