#!/bin/bash


SCALA_VERSION=2.10
PACKAGE=xxxx
MASTER=spark://10.10.104.15:7077
#MATSER=local
JAR="spark-wordcount-in-scala.jar"
JARS=spark-streaming-flume_2.10-1.0.1.jar,flume-ng-sdk-1.4.0.jar,avro-ipc-1.7.6.jar,avro-1.7.6.jar


CLASS=xxxx

SPARK_HOME=/home/hadoop/develop/spark-1.0.1-cdh3u6

arg1=hadoopdata1
arg2=4999
arg3=hadoopdata2
arg4=4999



$SPARK_HOME/bin/spark-submit \
  --master $MASTER \
  --class $CLASS \
  --jars $JARS \
  "$JAR" \
  "$arg1" \
  "$arg2" \
  "$arg3" \
  "$arg4"
  

