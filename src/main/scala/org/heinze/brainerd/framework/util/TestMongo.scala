package org.heinze.brainerd.framework.util

import com.mongodb.casbah.query._
import com.mongodb.casbah.util.bson.conversions._


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/30/12
 * Time: 3:01 PM
 */

//comment 1
//comment 2
//comment 3

object TestMongo extends App{

  import com.mongodb.casbah.commons._

  RNAMongoStorage.name = "RNA"
  RNAMongoStorage.collection = "base"
  RNAMongoStorage.host = "localhost"
  RNAMongoStorage.port = 27017


  RNAMongoStorage.connect

  //get 5
//  query("2011-04-30 23:57:42")


  //get a range
//  query2("2011-04-30 12:00:00", "2011-04-30 23:59:59")
//  query2("2010-04-30 12:00:00", "2010-04-30 23:59:59") //27975 records
//  query2("2010-04-30 12:00:00", "2010-04-30 12:59:59") //2643  records
//  query2("2010-04-30 12:00:00", "2010-04-30 12:10:10") //567  records
  query2("2010-04-30 12:00:00", "2010-04-30 12:01:00") //166  records




  RNAMongoStorage.disconnect

  def query(query: String): Unit = {
    import com.mongodb.casbah.commons.MongoDBObject
    import java.util.Date
    import com.mongodb.casbah.query._
    var pd: Date = null

    val aDate = query + " GMT"
    try {
      import java.util.Date
      pd = RNAMongoStorage.formDateTime.parse(aDate)

    }
    catch {
      case e: Exception => println("date error")
    }

    val q = MongoDBObject("STORY_DATE_TIME" -> pd)
    val res = RNAMongoStorage.mongoColl.find(q)

    res.foreach(i => println(i.getAs[String]("STORY_DATE_TIME")))

  }

  def query2(query1: String, query2: String): Unit = {
    import com.mongodb.casbah.commons.MongoDBObject
    import java.util.Date


    var d1: Date = null
    var d2: Date = null

    val date1 = query1 + " GMT"
    val date2 = query2 + " GMT"
    try {
      d1 = RNAMongoStorage.formDateTime.parse(date1)
      d2 = RNAMongoStorage.formDateTime.parse(date2)

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

    res.foreach(i => {

      counter = counter + 1
      val id =
      println(i)
    }   )

    println(counter)
  }
}
