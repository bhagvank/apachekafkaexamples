import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.streaming.Durations;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import java.util.regex.Pattern;

import javafx.util.Duration;



public class KafkaSparkIntegrationExample {
	

	
	public static void main(String[] arr)
	{
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("kafkastreamingclient");
		sparkConf.setMaster("local[*]");

		
		JavaStreamingContext kafkaStreamingContext = new JavaStreamingContext(sparkConf,Durations.seconds(1));
		Collection<String> topics = Arrays.asList("new_topic");
		
		Map<String,Object> kafkaParameters =  new HashMap<>();
		kafkaParameters.put("bootstrap.servers", "localhost:9092");
		kafkaParameters.put("value.deserializer", StringDeserializer.class);
		kafkaParameters.put("key.deserializer", StringDeserializer.class);
		kafkaParameters.put("group.id", "use_a_separate_group_id_for_each_stream_");
		kafkaParameters.put("auto.offset.reset", "latest");
		kafkaParameters.put("enable.auto.commit", false);
		JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = KafkaUtils.createDirectStream(kafkaStreamingContext, LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(topics, kafkaParameters)
				  );
		
	
		JavaDStream<String> message = kafkaStream.map(entry->entry.value());

		message.foreachRDD(kafkardd -> { 
									System.out.println("Count of messages:"+kafkardd.count());  
									kafkardd.foreach(value->System.out.println(value)); }
			  
							);

		kafkaStream.foreachRDD(rdd -> {
			  OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
			  rdd.foreachPartition(consumerRecords -> {
			    OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
			    
			   });
			  ((CanCommitOffsets) kafkaStream.inputDStream()).commitAsync(offsetRanges);

			});
		
	        
		kafkaStreamingContext.start();
		try {
			kafkaStreamingContext.awaitTermination();
		} catch (InterruptedException exception) {

			exception.printStackTrace();
		}
		
	}

}