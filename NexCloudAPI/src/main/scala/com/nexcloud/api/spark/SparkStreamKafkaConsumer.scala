package com.nexcloud.api.spark

import _root_.kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils

import com.nexcloud.util.KafkaCassandraConfigUtil


object SparkStreamingKafkaConsumer extends App {

  val brokers = KafkaCassandraConfigUtil.kafkaHost+":"+KafkaCassandraConfigUtil.kafkaPort

  val sparkConf = new SparkConf().setAppName("KafkaDirectStreaming").setMaster("local[2]")
    .set("spark.cassandra.connection.host", KafkaCassandraConfigUtil.hosts.toString())
    .set("spark.cassandra.auth.username", "cassandra")
  val ssc = new StreamingContext(sparkConf, Seconds(10))
  ssc.checkpoint("checkpointDir")

  val topicsSet = Set(KafkaCassandraConfigUtil.kafkaTopic)
  
  val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "group.id" -> "spark_streaming")
  
  
  val messages: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
	
  val datas: DStream[String] = messages.map { case (key, message) => message }
  
  DataHandler.createRealtimeData(ssc.sparkContext, datas)
  ssc.start()
  ssc.awaitTermination()
}

