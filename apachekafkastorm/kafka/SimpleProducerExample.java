
import java.util.Properties;


import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleProducerExample {
   
   public static void main(String[] args) throws Exception{
      
 
      if(args.length == 0){
         System.out.println("Enter the topic to be created ");
         return;
      }
      
      
      String exampleTopicName = args[0].toString();
      
  
      Properties properties = new Properties();
      
      properties.put("bootstrap.servers", "localhost:9092");
      
      properties.put("acks", "all");
      
      properties.put("retries", 0);
      
      properties.put("batch.size", 16384);
      
      properties.put("linger.ms", 1);
      
      properties.put("buffer.memory", 33554432);
      
      properties.put("key.serializer", 
         "org.apache.kafka.common.serialization.StringSerializer");
         
      properties.put("value.serializer", 
         "org.apache.kafka.common.serialization.StringSerializer");
      
      Producer<String, String> producer = new KafkaProducer
         <String, String>(properties);

      String[] strings = new String[10];
      strings[0] = "hi";
      strings[1] = "kafka test";
      strings[2] = "storm check";
      strings[3] = "spark job";
      strings[4] = "message";
      strings[5] = "operator";
      strings[6] = "modulo";
      strings[7] = "remainder";           
      strings[8] = "backtype";           
      strings[9] = "utility";           
      for(int i = 0; i < 10; i++)
      {

          producer.send(new ProducerRecord<String, String>(exampleTopicName,strings[i]));
          
             System.out.println("Message sent successfully "+ strings[i]);

            Thread.sleep(100);
      }      
          producer.close();
   }
}
