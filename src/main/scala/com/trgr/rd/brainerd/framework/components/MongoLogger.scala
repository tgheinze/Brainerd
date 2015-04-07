package com.trgr.rd.brainerd.framework.components

import com.trgr.rd.nlp.framework.component.MongoWriter
import com.trgr.rd.brainerd.framework.artifacts.CAS
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/23/12
 * Time: 9:39 AM
 */
class MongoLogger(parms: Map[Symbol, Any]) extends MongoWriter(parms) {

  override def process(c: CAS): Unit = {}

  def process(message: String): Unit = {
    val query = MongoDBObject("logEvent" -> message)
    mongoColl += query
  }
}
