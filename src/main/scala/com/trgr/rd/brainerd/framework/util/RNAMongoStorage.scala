package com.trgr.rd.nlp.framework.util


import scala.collection.immutable.List
import scala._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.mongodb.casbah._
import java.io.File


object RNAMongoStorage {


  //properties
  var host = "localhost"
  var port = 27017
  var name = "RNA"
  var collection = "base"


  var mongoConn: MongoConnection = _
  var mongoDB: MongoDB = _
  var mongoColl: MongoCollection = _


  val formDate = new SimpleDateFormat("yyyy-MM-dd z")
  formDate.setTimeZone(TimeZone.getTimeZone("GMT"));

  val formTime = new SimpleDateFormat("hh:mm:ss.SSS z")
  formTime.setTimeZone(TimeZone.getTimeZone("GMT"));

  val formDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z")
  formDateTime.setTimeZone(TimeZone.getTimeZone("GMT"));


  def connect() {
    try {
      mongoConn = MongoConnection(host, port)
      mongoDB = mongoConn(name)
      mongoColl = mongoDB(collection)

    } catch {
      case e: Exception => println("Unable to connect to storage")
    }
  }

  def disconnect {
    try {
      mongoConn.close()
    } catch {
      case e: Exception => println("Unable to connect to storage")
    }
  }


  def create = {

    try {
      mongoDB = mongoConn(name)
      mongoColl = mongoDB(collection)
    }
    catch {
      case e: Exception => println(e.getStackTrace)
    }

  }

  def query(query1: String, query2: String): List[String] = {
    import com.mongodb.casbah.commons.MongoDBObject
    import java.util.Date
    import scala.collection.mutable.ListBuffer


    var d1: Date = null
    var d2: Date = null

    val date1 = query1 + " GMT"
    val date2 = query2 + " GMT"
    try {
      d1 = formDateTime.parse(date1)
      d2 = formDateTime.parse(date2)

    }
    catch {
      case e: Exception => println("date error")
    }

    val q1 = MongoDBObject("STORY_DATE_TIME" -> d1)
    val q2 = MongoDBObject("STORY_DATE_TIME" -> d2)

    import com.mongodb.casbah.query._

    val q = "STORY_DATE_TIME" $gte d1 $lte d2

    val res = RNAMongoStorage.mongoColl.find(q)

    var counter = 0
    var oids: ListBuffer[String] = new ListBuffer[String]()

    res.foreach(i => {
      val oid: ObjectId =  i.getAs[ObjectId]("_id").get
      oids += oid.toString
    } )

    oids.toList
  }



}

