package com.nexcloud.api.cassandra

import com.datastax.driver.core._
import com.nexcloud.util.ConfigUtil._
import org.slf4j.LoggerFactory
import collection.JavaConversions._

trait CassandraConnection {

  val logger = LoggerFactory.getLogger(getClass.getName)
  val defaultConsistencyLevel = ConsistencyLevel.valueOf(writeConsistency)

  val cassandraConn: Session = {
    val cluster = new Cluster.Builder().withClusterName("Cassandra Cluster").
      addContactPoints(hosts).
      withPort(port).
      withQueryOptions(new QueryOptions().setConsistencyLevel(defaultConsistencyLevel)).build
    val session = cluster.connect
    println(s"Keyspace [${cassandraKeyspace}]")

    session.execute(s"CREATE KEYSPACE IF NOT EXISTS  ${cassandraKeyspace} WITH REPLICATION = " +
      s"{ 'class' : 'SimpleStrategy', 'replication_factor' : ${replicationFactor} }")
    session.execute(s"USE ${cassandraKeyspace}")






    val query = s"CREATE TABLE IF NOT EXISTS ${cassandraTable} " +
      s"(${cassandraColumn}) "

    createTables(session, query)

    session
  }

  def createTables(session: Session, createTableQuery: String): ResultSet = session.execute(createTableQuery)

}

object CassandraConnection extends CassandraConnection
