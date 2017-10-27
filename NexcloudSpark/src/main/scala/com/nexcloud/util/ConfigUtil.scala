package com.nexcloud.util

import scala.collection.JavaConversions._

import com.typesafe.config.ConfigFactory


object ConfigUtil {

  val config 				= ConfigFactory.load()

  val invokeClass = config.getString("app.invoke_class")
  val invokeMethod = config.getString("app.invoke_method")

  val kafkaHost	 			= config.getString("kafka.host")
  val kafkaGroup 			= config.getString("kafka.group")
  val kafkaPort 			= config.getString("kafka.port")
  val kafkaTopic 			= config.getString("kafka.topic")
  val kafkaZookeeper		= config.getString("kafka.zookeeper")
  
  
  val port 					= config.getInt("cassandra.port")
  val hosts 				= config.getString("cassandra.hosts")
  val cassandraKeyspace 	= config.getString("cassandra.keyspace")
  val cassandraTable 		= config.getString("cassandra.table")
  val cassandraColumn 		= config.getString("cassandra.column")
  val replicationFactor 	= config.getString("cassandra.replication_factor").toInt
  val readConsistency 		= config.getString("cassandra.read_consistency")
  val writeConsistency 		= config.getString("cassandra.write_consistency")

}
