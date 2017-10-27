package com.nexcloud.api.cassandra

import java.util.Date

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object CassandraOperation extends CassandraConnection {

  def insertCassandra(listJson: List[String], tableName: String) = {
    listJson.map(json => cassandraConn.execute(s"INSERT INTO $tableName JSON '$json'"))
  }
}