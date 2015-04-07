package com.trgr.rd.nlp.framework.util

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/1/12
 * Time: 1:43 PM
 */

object Test3 extends App {


  import net.liftweb.json._
  implicit val formats = DefaultFormats
  case class Meta(OID: String)
  val meta = parse("""{"OID":"2342324324"}""")
  val md = meta.extract[Meta]
  println(md.OID)

  import com.mongodb.casbah.commons.MongoDBObject
  import com.mongodb.casbah.query._
  import com.mongodb.DBObject

  RNAMongoStorage.name = "RNA"
  RNAMongoStorage.collection = "base"
  RNAMongoStorage.host = "localhost"
  RNAMongoStorage.port = 27017


  RNAMongoStorage.connect

  var oid: ObjectId = _

  query("2011-04-30 23:57:42")

  val q = MongoDBObject("_id" -> oid)
  val res = RNAMongoStorage.mongoColl.find(q)

  res.foreach(i => println(i.getAs[String]("STORY_DATE_TIME")))

  val id1 = "5005ff7d0364405ca38520e4"
  val q1 = MongoDBObject("_id" -> new ObjectId(id1))
  val res1 = RNAMongoStorage.mongoColl.find(q1)

  res1.foreach(i => println(i.getAs[String]("STORY_DATE_TIME")))


  val mo = MongoDBObject("OID" -> "234242424234234")
  println(mo)
  val dbo: DBObject = mo.asInstanceOf[DBObject]
  println(dbo.get("OID"))

  def query(query: String): Unit = {

    import java.util.Date
    import com.mongodb.casbah.MongoCursor


    var pd: Date = null

    val aDate = query + " GMT"
    try {

      pd = RNAMongoStorage.formDateTime.parse(aDate)

    }
    catch {
      case e: Exception => println("date error")
    }

    val q = MongoDBObject("STORY_DATE_TIME" -> pd)
    val res: MongoCursor = RNAMongoStorage.mongoColl.find(q)

    res.foreach(i => {
      oid = i.getAs[ObjectId]("_id").get
      println(oid)
    })

  }

}
