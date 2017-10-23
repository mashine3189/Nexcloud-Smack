package com.nexcloud.util

import scala.collection.JavaConversions._

import com.typesafe.config.ConfigFactory


object KafkaCassandraConfigUtil {

  val config 				= ConfigFactory.load()

  val consumerKey 			= config.getString("app.consumerKey")
  val consumerSecret 		= config.getString("app.consumerSecret")
  val accessToken 			= config.getString("app.accessToken")
  val accessTokenSecret 	= config.getString("app.accessTokenSecret")
  
  val kafkaHost	 			= config.getString("kafka.host")
  val kafkaGroup 			= config.getString("kafka.group")
  val kafkaPort 			= config.getString("kafka.port")
  val kafkaTopic 			= config.getString("kafka.topic")
  val kafkaZookeeper		= config.getString("kafka.zookeeper");
  
  
  val port 					= config.getInt("cassandra.port")
  val hosts 				= config.getStringList("cassandra.hosts").toList
  val cassandraKeyspace 	= config.getString("cassandra.keyspace")
  val cassandraTable 		= config.getString("cassandra.table")
  val cassandraColumn 		= config.getString("cassandra.column")
  val replicationFactor 	= config.getString("cassandra.replication_factor").toInt
  val readConsistency 		= config.getString("cassandra.read_consistency")
  val writeConsistency 		= config.getString("cassandra.write_consistency")

}
