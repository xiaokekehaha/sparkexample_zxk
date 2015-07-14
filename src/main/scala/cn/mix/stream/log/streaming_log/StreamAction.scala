package cn.mix.stream.log.streaming_log

import org.apache.spark.streaming.dstream.DStream

/**
 * @author surq
 * @since 2014.07.14
 * 所有log处理都要实现此类<br>
 */
abstract class StreamAction {
  
 def run(logtype:String, inputStream:DStream[Array[(String, String)]], logSteps: Int): DStream[Array[(String, String)]]
  def printInfo(className: Class[_], msg: String) {
    LogTools.mixInfo("["+ className.getName() +"] INFO: " + msg)
  }
  def printDebug(className: Class[_], msg: String) {
    LogTools.mixDebug("["+ className.getName() +"] DEBUG: " + msg)
  }
  def printError(className: Class[_], msg: String) {
    LogTools.mixError("["+ className.getName() +"] ERROR: " + msg)
  }
  def printError(className: Class[_], msg: String,U:Unit) {
    LogTools.mixError("["+ className.getName() +"] ERROR: " + msg ,U)
  }
}