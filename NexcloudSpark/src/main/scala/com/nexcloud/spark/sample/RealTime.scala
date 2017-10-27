package com.nexcloud.spark.sample

import com.nexcloud.util.ConfigUtil
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.dstream.DStream

import scala.reflect.runtime.{universe => ru}

object RealTime {

  def createViewForFriendCount(sparkContext: SparkContext, datas: DStream[String]) = {
    datas.foreachRDD { (rdd: RDD[String], time: Time) =>
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      val datas: DataFrame = spark.sqlContext.read.json(rdd)
      datas.createOrReplaceTempView("tempTable")
      val wordCountsDataFrame: DataFrame = spark.sql("SELECT userId,createdAt, friendsCount from tempTable Where friendsCount > 500 ")
      val res: DataFrame = wordCountsDataFrame.withColumnRenamed("userId","userid").withColumnRenamed("createdAt","createdat").withColumnRenamed("friendsCount","friendscount")
      res.write.mode(SaveMode.Append)
        .format("org.apache.spark.sql.cassandra")
        .options(Map( "table" -> "friendcountview", "keyspace" -> "realtime_view"))
        .save()
      wordCountsDataFrame.show(false)
      wordCountsDataFrame.printSchema()

    }
  }
}
