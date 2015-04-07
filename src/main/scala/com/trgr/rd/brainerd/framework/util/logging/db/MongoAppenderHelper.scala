package com.trgr.rd.util.logging.db

import com.trgr.rd.brainerd.framework.components.MongoLogger

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/23/12
 * Time: 9:16 AM
 */


class MongoAppenderHelper(val host: String, val port: Int, val db: String, val collection: String) {

  println("Connecting to " + host)

  var parameters =  Map[Symbol, Any]()
  parameters += ('mongoHost -> host)
  parameters += ('mongoPort -> port)
  parameters += ('mongoOutDB -> db)
  parameters += ('mongoOutCollection -> collection)

  val logger = new MongoLogger(parameters)



  def append(logEvent: String) = {
    logger.process(logEvent)
  }

  def close() = {
    println("shutting down db connection")
    logger disconnect
  }
}
