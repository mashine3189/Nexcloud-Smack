package com.nexcloud.sample

import java.util.Properties

import akka.actor._
import kafka.consumer.{Consumer, ConsumerConfig, ConsumerIterator, ConsumerTimeoutException}
import kafka.consumer._
import org.slf4j.LoggerFactory
import java.util.HashMap

import kafka.serializer.StringDecoder
import kafka.utils.VerifiableProperties

import scala.collection.mutable
import com.nexcloud.util.ConfigUtil
import com.nexcloud.api.kafka.Consumer
import com.nexcloud.api.cassandra._

import scala.concurrent.duration.DurationInt

case object GiveMeWork

class KafkaMessageConsumer(consumer: Consumer) extends Actor  {

  implicit val dispatcher = context.dispatcher

  val initialDelay = 1000 milli
  val interval = 1 seconds


  context.system.scheduler.schedule(initialDelay, interval, self, GiveMeWork)

  def receive: PartialFunction[Any, Unit] = {
    case GiveMeWork => var data = consumer.read
                        if( !data.isEmpty )
                          CassandraOperation.insertCassandra( data, ConfigUtil.cassandraTable)
  }

}

object CassandraKafkaConsumer extends App {

  println(s"Inti kafkaTopic[$ConfigUtil.kafkaTopic], Keyspace [${ConfigUtil.cassandraKeyspace}], Table[${ConfigUtil.cassandraTable}]")

  val actorSystem = ActorSystem("KafkaActorSystem")

  println(s" After kafkaTopic[$ConfigUtil.kafkaTopic], Keyspace [${ConfigUtil.cassandraKeyspace}], Table[${ConfigUtil.cassandraTable}]")

  // Kafka
  val consumer = actorSystem.actorOf(Props(new KafkaMessageConsumer(new Consumer)))

  consumer !
  GiveMeWork
}
