import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import backtype.storm.spout.SchemeAsMultiScheme;
import storm.kafka.trident.GlobalPartitionInformation;
import storm.kafka.ZkHosts;
import storm.kafka.Broker;
import storm.kafka.StaticHosts;
import storm.kafka.BrokerHosts;
import storm.kafka.SpoutConfig;
import storm.kafka.KafkaConfig;
import storm.kafka.KafkaSpout;
import storm.kafka.StringScheme;

public class KafkaStormIntegrationExample {
   public static void main(String[] args) throws Exception{
      Config stormConfig = new Config();
      stormConfig.setDebug(true);
      stormConfig.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
      String zkConnString = "localhost:2181";
      String topic = "kafka_topic_phrases";
      BrokerHosts hosts = new ZkHosts(zkConnString);
      
      SpoutConfig spoutConfiguration = new SpoutConfig (hosts, topic, "/" + topic,    
         UUID.randomUUID().toString());
      spoutConfiguration.bufferSizeBytes = 1024 * 1024 * 4;
      spoutConfiguration.fetchSizeBytes = 1024 * 1024 * 4;
      spoutConfiguration.forceFromStart = true;
      spoutConfiguration.scheme = new SchemeAsMultiScheme(new StringScheme());

      TopologyBuilder topologyBuilder = new TopologyBuilder();
      topologyBuilder.setSpout("kafka-spout", new KafkaSpout(spoutConfiguration));
      topologyBuilder.setBolt("word-spitter", new SplitBolt()).shuffleGrouping("kafka-spout");
      topologyBuilder.setBolt("word-counter", new CountBolt()).shuffleGrouping("word-spitter");
         
      LocalCluster localCluster = new LocalCluster();
      localCluster.submitTopology("KafkaStormSample", stormConfig, topologyBuilder.createTopology());

      Thread.sleep(10000);
      
      localCluster.shutdown();
   }
}