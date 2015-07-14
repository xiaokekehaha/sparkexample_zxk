package cn.chinahadoop.scala;




import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;





public class SparkSQLGuide implements  Serializable {

	

	public static void readSeqFile() 
	{
		SparkConf conf = new SparkConf()
				.setAppName("spark sql query by seqFile ......").setMaster("local[2]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaPairRDD<Text, Text> pairRDD = sc.sequenceFile("file:///d:/12.seq-r-00000", Text.class,Text.class);
		
		
		List<Tuple2<Text, Text>> collect = pairRDD.collect();
		
		Iterator<Tuple2<Text, Text>> iterator = collect.iterator();
		while(iterator.hasNext()){
			Tuple2<Text, Text> tuple = iterator.next();
			System.out.println("key=" + tuple._1.toString() + "==value:" + tuple._2.toString());
		}

	}

	public static void main(String[] args) {
		readSeqFile();
		
	
	}
	

}
