package com.seu.kafka2storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;


public class KafkaTopology {

	public static void main(String[] args) throws Exception {
		
		String topic = "wordcount";
		String zkRoot = "/storm";
		String spoutId = "KafkaSpout";
		BrokerHosts brokerHosts = new ZkHosts("hadoop02:2181,hadoop03:2181,hadoop04:2181"); 
		SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "wordcount", zkRoot, spoutId);
//		spoutConfig.ignoreZkOffsets = true;
		spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());
		TopologyBuilder builder = new TopologyBuilder();
		//设置一个spout用来从kaflka消息队列中读取数据并发送给下一级的bolt组件，此处用的spout组件并非自定义的，而是storm中已经开发好的KafkaSpout
		builder.setSpout("KafkaSpout", new KafkaSpout(spoutConfig),4);
		builder.setBolt("word-spilter", new WordSpliter(),4).shuffleGrouping("KafkaSpout");
		builder.setBolt("writer", new WriterBolt(), 4).shuffleGrouping("word-spilter");
		Config conf = new Config();
		conf.setNumWorkers(4);
		conf.setNumAckers(0);
		conf.setDebug(false);
		
		//LocalCluster用来将topology提交到本地模拟器运行，方便开发调试
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("WordCount", conf, builder.createTopology());
		
		//提交topology到storm集群中运行
//		StormSubmitter.submitTopology("wordcount", conf, builder.createTopology());
	}

}
