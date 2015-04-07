package com.trgr.rd.nlp.framework.component

import com.trgr.rd.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */




class MongoReader(var parms: Map[Symbol, Any]) extends Component {
  import com.mongodb.casbah.{MongoCollection, MongoDB, MongoConnection}

  def this() =  this(null)



  var mongoConn: MongoConnection = _
  var mongoDB: MongoDB = _
  var mongoColl: MongoCollection = _
  var host: String = _
  var port: Int = _
  var db: String = _
  var collection: String = _

  parms match {
    case p: Map[Symbol, Any] => {
      host = p.get('mongoHost).get.asInstanceOf[String]
      port = p.get('mongoPort).get.asInstanceOf[Int]
      db = p.get('mongoDB).get.asInstanceOf[String]
      collection = p.get('mongoCollection).get.asInstanceOf[String]
    }
    case _ =>
  }


  connect


  def process(c: CAS): Unit = {
    import com.trgr.rd.nlp.framework.util.JSONHelper
    import com.mongodb.casbah.commons.MongoDBObject
    import com.mongodb.casbah.query._
    import org.bson.types.ObjectId

    //Extract the mongo object id that was put into the cas metadata,
    //build a request, get the record, extract the text and store in the view
    val oid = JSONHelper.extractOID(c.metadata)
    val query = MongoDBObject("_id" -> new ObjectId(oid))
    val res1 = mongoColl.findOne(query)

    c('text).setText(res1.get.get("TAKE_TEXT").toString)
//    println(c('text).getText())
  }
  override def finalize() = {
    logger.info("cleaning up...")
    disconnect
  }
  def connect() {
    try {
      import com.mongodb.casbah.MongoConnection
      mongoConn = MongoConnection(host, port)
      mongoDB = mongoConn(db)
      mongoColl = mongoDB(collection)

    } catch {
      case e: Exception => logger.error("Unable to connect to storage")
    }
  }

  def disconnect {
    try {
      mongoConn.close()
    } catch {
      case e: Exception => println("Unable to connect to storage")
    }
  }
}

object MongoReader extends ComponentFactory {
  def apply() = new MongoReader()
  def apply(parms: Map[Symbol, Any]) = new MongoReader(parms)
}

