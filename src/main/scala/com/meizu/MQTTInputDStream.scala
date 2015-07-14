/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.streaming.mqtt

import scala.collection.Map
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import scala.reflect.ClassTag
import java.util.Properties
import java.util.concurrent.Executors
import java.io.IOException
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver
import com.taobao.metamorphosis.client.MetaMessageSessionFactory
import com.taobao.metamorphosis.client.MetaClientConfig
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig
import com.taobao.metamorphosis.client.consumer.ConsumerConfig
import com.taobao.metamorphosis.client.consumer.MessageListener
import com.taobao.metamorphosis.Message
import java.util.concurrent.Executor;
/**
 * Input stream that subscribe messages from a Mqtt Broker.
 * Uses eclipse paho as MqttClient http://www.eclipse.org/paho/
 * @param brokerUrl Url of remote mqtt publisher
 * @param topic topic name to subscribe to
 * @param storageLevel RDD storage level.
 */

private[streaming] class MQTTInputDStream(
  @transient ssc_ : StreamingContext,
  brokerUrl: String,
  topic: String,
  storageLevel: StorageLevel) extends ReceiverInputDStream[String](ssc_) {

 def getReceiver():Receiver[String]={
   
   new MQTTReceiver(brokerUrl,topic,storageLevel)
 }
}

private[streaming] class MQTTReceiver(
  brokerUrl: String,
  topic: String,
  storageLevel: StorageLevel) extends Receiver[String](storageLevel) {

  def onStop() {

  }

  def onStart() {

    val sessionFactory = new MetaMessageSessionFactory(getmetaQconfig)

    //val topic = "ORION_TEST";
    // consumer group
    val group = "ORION_TEST_GROUP";
    // create consumer,强烈建议使用单例
    val consumerConfig = new ConsumerConfig(group);
    // 默认最大获取延迟为5秒，这里设置成100毫秒，请根据实际应用要求做设置。
    consumerConfig.setMaxDelayFetchTimeInMills(100);
    val consumer = sessionFactory
      .createConsumer(consumerConfig);
    // subscribe topic

    val message: MessageListener = new MessageListener {

      override def recieveMessages(message: Message) {
        System.out.println("Receive message "
          + new String(message.getData()));
      }
      override  def getExecutor():Executor={
        null
        }
      

    }
    consumer.subscribe(topic, 1024*1024, message)

    // complete subscribe
    consumer.completeSubscribe();

  }


  def getmetaQconfig(): MetaClientConfig = {

    val metaClientConfig = new MetaClientConfig();
    val zkConfig = new ZKConfig();
    zkConfig.zkConnect = "172.16.200.239:2181,172.16.200.233:2181,172.16.200.234:2181";
    zkConfig.zkRoot = "/meta";
    metaClientConfig.setZkConfig(zkConfig);
    return metaClientConfig;

  }
}
