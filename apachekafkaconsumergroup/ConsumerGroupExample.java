import java.util.Properties;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerGroupExample {
	
   public static void main(String[] args) throws Exception {
      if(args.length < 2){
         System.out.println("Input this command: consumer <topic> <consumerGroupname>");
         return;
      }
      
      String topic = args[0].toString();
      String consumerGroup = args[1].toString();
	  
	  System.out.println(topic);
	  System.out.println(consumerGroup);
	  
      Properties properties = new Properties();
      properties.put("bootstrap.servers", "localhost:9092");
      properties.put("group.id", consumerGroup);
      properties.put("enable.auto.commit", "true");
      properties.put("auto.commit.interval.ms", "1000");
      properties.put("session.timeout.ms", "30000");
      properties.put("key.deserializer",          
         "org.apache.kafka.common.serialization.StringDeserializer");
      properties.put("value.deserializer", 
         "org.apache.kafka.common.serialization.StringDeserializer");
      KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
      
      consumer.subscribe(Arrays.asList(topic));
      System.out.println("Subscription to topic complete " + topic);
      int i = 0;
         
      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
               System.out.printf("offset value = %d, key = %s, value = %s\n", 
               record.offset(), record.key(), record.value());
      }     
   }  
}