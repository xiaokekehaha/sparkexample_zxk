/*package cn.chinahadoop.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

public final class JavaFlumeEventCount {
  private JavaFlumeEventCount() {
  }

  public static void main(String[] args) {

    String host = args[0];
    int port = Integer.parseInt(args[1]);

    Duration batchInterval = new Duration(Integer.parseInt(args[2]));
    SparkConf sparkConf = new SparkConf().setAppName("JavaFlumeEventCount");
    JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, batchInterval);
    JavaReceiverInputDStream<SparkFlumeEvent> flumeStream = FlumeUtils.createStream(ssc, host, port);

    flumeStream.count();

    flumeStream.count().map(new Function<Long, String>() {

      public String call(Long in) {
        return "Received " + in + " flume events.";
      }
    }).print();

    ssc.start();
    ssc.awaitTermination();
  }
}*/