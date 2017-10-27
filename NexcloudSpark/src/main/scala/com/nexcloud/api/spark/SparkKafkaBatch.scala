package com.nexcloud.api.spark

import akka.actor.{Actor, ActorSystem, Props}
import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}
import com.nexcloud.util.ConfigUtil
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

import org.apache.spark._
import org.apache.spark.streaming._
import com.datastax.spark.connector.streaming._
import org.apache.spark.rdd._

class BatchProcessingUnit {

  val sparkConf = new SparkConf().setAppName("KafkaDirectStreaming").setMaster("local[2]")
    .set("spark.cassandra.connection.host", ConfigUtil.hosts)
    .set("spark.cassandra.auth.username", "cassandra")

  val sc = new StreamingContext(sparkConf, Seconds(10))
  sc.checkpoint("checkpointDir")

  def start: Unit ={
    val rdd = sc.cassandraTable(ConfigUtil.cassandraKeyspace, ConfigUtil.cassandraTable)

    // 실제 사용 로직부분
    // 조거등 쿼리 
    val result = rdd.select("userid","createdat","friendscount").where("friendsCount > ?", 500)
    result.saveToCassandra("batch_view","friendcountview",SomeColumns("userid","createdat","friendscount"))
    result.foreach(println)
  }
}

import scala.concurrent.duration.DurationInt

case object StartBatchProcess

class BatchProcessingActor(processor: BatchProcessingUnit) extends Actor  {

  implicit val dispatcher = context.dispatcher

  val initialDelay = 1000 milli
  val interval = 60 seconds


  context.system.scheduler.schedule(initialDelay, interval, self, StartBatchProcess)

  def receive: PartialFunction[Any, Unit] = {

    case StartBatchProcess => processor.start

  }

}

object BatchProcessor extends App {

  val actorSystem = ActorSystem("BatchActorSystem")

  val processor = actorSystem.actorOf(Props(new BatchProcessingActor(new BatchProcessingUnit)))

  processor ! StartBatchProcess

}
