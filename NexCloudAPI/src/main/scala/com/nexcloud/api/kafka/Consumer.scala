package com.nexcloud.api.kafka

import java.util.Properties

import akka.actor._
import kafka.consumer.{Consumer, ConsumerConfig, ConsumerIterator, ConsumerTimeoutException}
import kafka.consumer._
import org.slf4j.LoggerFactory
import java.util.HashMap

import kafka.serializer.StringDecoder
import kafka.utils.VerifiableProperties
import scala.collection.mutable

import com.nexcloud.util.KafkaCassandraConfigUtil


class KafkaConsumer {
  val logger = LoggerFactory.getLogger(this.getClass)

  private val props = new Properties
  props.put("group.id", KafkaCassandraConfigUtil.kafkaGroup)
  props.put("bootstrap.servers", KafkaCassandraConfigUtil.kafkaHost+":"+KafkaCassandraConfigUtil.kafkaPort)
  props.put("zookeeper.connect", KafkaCassandraConfigUtil.kafkaZookeeper)
  props.put("enable.auto.commit", "true")
  props.put("auto.offset.reset", "smallest")
  props.put("consumer.timeout.ms", "5000")
  props.put("auto.commit.interval.ms", "1000")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

  private val noOfStreams =1
  private val batchSize = 100
  private val topic = KafkaCassandraConfigUtil.kafkaTopic
  private val consumerConnector = Consumer.create(new ConsumerConfig(props))
  private val iterator: ConsumerIterator[Array[Byte], Array[Byte]] = consumerConnector.createMessageStreams(Map(topic -> noOfStreams)).mapValues(_.head)(topic).iterator()


  def read =
    try {
      
      if (iterator.hasNext()) {
        println(s"Got message   ::::::::::::::::::: ")
        readTopic(topic, iterator)
      }
      else
        println("$$$$$ no data::::::::::")

    } catch {
      case timeOutEx: ConsumerTimeoutException =>
        println("$$$Getting time out  when reading message", timeOutEx)
      case ex: Exception =>
        println(s"Not getting message from ", ex)
    }


  private def readTopic(topic: String, iterator: ConsumerIterator[Array[Byte], Array[Byte]]) : List[String] = {
    var batch = List.empty[String]
    while (hasNext(iterator) && batch.size < batchSize) {
      batch = batch :+ (new String(iterator.next().message()))
    }
    if (batch.isEmpty)
    {
    	throw new IllegalArgumentException(s"$topic is  empty")
    	//return batch
    }
    else
    { 
    	//CassandraOperation.insertTweets(batch)
    	return batch
    }
  }

  private def hasNext(it: ConsumerIterator[Array[Byte], Array[Byte]]): Boolean =
    try {
      it.hasNext()
    }catch {
      case timeOutEx: ConsumerTimeoutException =>
        println("Getting time out  when reading message :::::::::::::: ")
        false
      case ex: Exception =>
        println("Getting error when reading message :::::::::::::::::  ", ex)
        false
    }

}

import scala.concurrent.duration.DurationInt

case object GiveMeWork

class KafkaMessageConsumer(consumer: KafkaConsumer) extends Actor  {

  implicit val dispatcher = context.dispatcher

  val initialDelay = 1000 milli
  val interval = 1 seconds


  context.system.scheduler.schedule(initialDelay, interval, self, GiveMeWork)

  def receive: PartialFunction[Any, Unit] = {

    case GiveMeWork => consumer.read
  }

}

object KafkaConsumer extends App {

  val actorSystem = ActorSystem("KafkaActorSystem")

  val consumer = actorSystem.actorOf(Props(new KafkaMessageConsumer(new KafkaConsumer)))

  consumer !
  GiveMeWork
}
