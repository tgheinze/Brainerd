package com.trgr.rd.nlp.framework.component

import com.trgr.rd.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */



class MongoWriter(var parms: Map[Symbol, Any]) extends Component {

  import com.mongodb.casbah.{MongoCollection, MongoDB, MongoConnection}

  def this() = this(null)


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
      db = p.get('mongoOutDB).get.asInstanceOf[String]
      collection = p.get('mongoOutCollection).get.asInstanceOf[String]
    }
    case _ =>
  }

  connect


  def process(c: CAS): Unit = {
    c('text).getAll('root).foreach(annot => {
      import com.mongodb.casbah.commons.MongoDBObject
      import org.bson.types.ObjectId
      import com.trgr.rd.nlp.framework.util.JSONHelper
//      println("annot ---------> " + annot.text + "  " + annot.name)
      val oid = JSONHelper.extractOID(c.metadata)
      val query = MongoDBObject("ref_id" -> new ObjectId(oid), "type" -> annot.name.name,
                                "value" -> annot.text)
      mongoColl += query
    })

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

object MongoWriter extends ComponentFactory {
  def apply() = new MongoWriter()
  def apply(parms: Map[Symbol, Any]) = new MongoWriter(parms)
}

