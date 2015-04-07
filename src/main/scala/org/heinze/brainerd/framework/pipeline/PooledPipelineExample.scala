package org.heinze.brainerd.framework.pipeline

import com.weiglewilczek.slf4s.Logging
import org.heinze.brainerd.framework.artifacts.CAS
import org.heinze.brainerd.framework.components.{MongoWriter, Annotator, RADTokenizerSentenceSplitter, MongoReader}
import org.heinze.brainerd.framework.util.{RNAMongoStorage, PooledPipeline}


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 11:54 AM
 */

object PooledPipelineExample extends App with Logging {



  logger.info("Starting app")

  //Define any global parameters that the components need
  var parameters =  Map[Symbol, Any]()
  parameters += ('mongoHost -> "localhost")
  parameters += ('mongoPort -> 27017)
  parameters += ('mongoDB -> "RNA")
  parameters += ('mongoCollection -> "base")
  parameters += ('mongoOutDB -> "RNAPlus")
  parameters += ('mongoOutCollection -> "entitiesPP2")

  var parmSet1: Map[Symbol, Any] =  Map[Symbol, Any]()
  parmSet1 += ('name -> "Terry")
  var parmSet2: Map[Symbol, Any] =  Map[Symbol, Any]()
  parmSet2 += ('name -> "Heinze")

  //Create a pooled pipeline:
  // - define each pool of components by adding a tuple with the component factory
  //   and how many component instances to create
  //   and an optional reference to a list of system parameters
  val pipeline = new PooledPipeline
  pipeline ++ (MongoReader, 4, parameters)
  pipeline ++ (RADTokenizerSentenceSplitter, 4)
  pipeline ++ (Annotator, 2, parmSet1)
  pipeline ++ (Annotator, 2, parmSet2)
  pipeline ++ (MongoWriter, 2, parameters)

  //Get a list of RNA records:
  // - query by a date range
  // - return a list of mongo object ids
  val oids = getRecords("2010-04-30 12:00:00", "2010-04-30 12:01:00") //166  records

  var casPool: List[CAS] = Nil

  val startTime = System.nanoTime()

  //For each record, create a CAS
  // - add to the CAS's metadata field, a JSON object that has the object id
  // - add the CAS to the list of active CASes
  // - pass the CAS to the pooled pipeline, which will send the CAS as a message to
  //   one of the components in the first pool (actually the Actor wrapper for the component)
  oids.foreach(oid => {
    import com.mongodb.casbah.commons.MongoDBObject
    val cas = new CAS()
    val mo = MongoDBObject("OID" -> oid)
    cas.metadata = mo.toString
    updatePool(casPool, cas)
    pipeline run cas
  })

  //When an Actor in the last component pool completes, it sets the CAS to inactive
  // - the inactive cas is removed from the list
  // - when all CASes are inactive, the application terminates all of the Actors
  while (checkPool(casPool)) {}
  pipeline terminate


  val elapsedTime = System.nanoTime() - startTime
  println(elapsedTime / 1000000.0 + " ms")




  //End of pipeline


  //************************************************** functions
  def checkPool(casPool: List[CAS]): Boolean = {
    updatePool(casPool, null)
    val state = (false /: casPool)(_ || _.active)
    state
  }



  def updatePool(cp: List[CAS], cas: CAS) = synchronized {
    import scala.collection.mutable.ListBuffer
    var newPool: ListBuffer[CAS] = ListBuffer[CAS]()
    cp.foreach(c => if(c.active) newPool += c)
    //    println("active pool size: " + newPool.size)
    casPool = newPool.toList
    cas match {
      case c: CAS => casPool = cas :: casPool
      case _ =>
    }
  }


  def getRecords(start: String, end: String): List[String] = {
    RNAMongoStorage.name = "RNA"
    RNAMongoStorage.collection = "base"
    RNAMongoStorage.host = "localhost"
    RNAMongoStorage.port = 27017

    RNAMongoStorage.connect

    val oids = RNAMongoStorage.query("2010-04-30 12:00:00", "2010-04-30 12:01:00") //166  records

    RNAMongoStorage.disconnect

    oids
  }

}

