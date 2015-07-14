package cn.chinahadoop.streaming;



import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;


public class JavaKafkaEventCount {
	public JavaKafkaEventCount() {

	}

	public static void main(String[] args) {

		String zkQuorum = args[0];
		String groupId = args[1];
		Map<String, Integer> topics = new HashMap<String, Integer>();
		topics.put(args[2], 6);

		Duration batchInterval = new Duration(Integer.parseInt(args[3]));
		SparkConf sparkConf = new SparkConf().setAppName("JavaKafkaEventCount");
		JavaStreamingContext ssc = new JavaStreamingContext(sparkConf,
				batchInterval);
		
		/**
		 * createStream
		 * jssc JavaStreamingContext object
		 * zkQuorum Zookeeper quorum (hostname:port,hostname:port,..)
		 * groupId The group id for this consumer
		 * topics Map of (topic_name -> numPartitions) to consume. Each partition is consumed in its own thread
		 */
		JavaPairReceiverInputDStream<String, String> kafkaStream = KafkaUtils
				.createStream(ssc, zkQuorum, groupId, topics);

		kafkaStream.count();

		kafkaStream.count().map(new Function<Long, String>() {
			public String call(Long in) {
				return "Received " + in + " kafka event.";
			}
		}).print();

		ssc.start();
		ssc.awaitTermination();
	}
}
