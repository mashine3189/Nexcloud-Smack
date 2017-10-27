package com.nexcloud.api.spark

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.dstream.DStream
import scala.reflect.runtime.{universe => ru}
import com.nexcloud.util.ConfigUtil

object DataHandler {


  def createRealtimeData(sparkContext: SparkContext, datas: DStream[String]) = {
    //createViewForFriendCount(sparkContext, datas)
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val module = m.staticModule(ConfigUtil.invokeClass)
    val im = m.reflectModule(module)
    val method = im.symbol.info.decl(ru.TermName(ConfigUtil.invokeMethod)).asMethod

    val objMirror = m.reflect(im.instance)
    objMirror.reflectMethod(method)(sparkContext, datas)

  }
/*
  // 실제 개발자의 개발 로직이 들어가는 부분
  // 개발자가 임의로 개발가능
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
*/

}
