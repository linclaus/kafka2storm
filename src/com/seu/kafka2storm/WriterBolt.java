package com.seu.kafka2storm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
/**
 * 将数据写入文件
 * @author duanhaitao@itcast.cn
 *
 */
public class WriterBolt extends BaseBasicBolt {

	private static final long serialVersionUID = -6586283337287975719L;
	
	FileWriter writer;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		try {
			writer = new FileWriter("/home/hadoop/stormdata/" + "wordcount"+UUID.randomUUID());///home/storm/stormdata/
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
	
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String s = input.getString(0);
		try {
			writer.write(s);
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
