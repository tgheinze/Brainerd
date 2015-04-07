package org.heinze.brainerd.framework.pipeline

import com.weiglewilczek.slf4s.Logging
import org.heinze.brainerd.framework.artifacts.CAS
import org.heinze.brainerd.framework.util.{RNAMongoStorage}
import org.heinze.brainerd.framework.util.BasicPipeline

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 11:54 AM
 */

object BasicPipelineExample extends App with Logging {




  logger.info("Starting app")

  //Define any global parameters that the components need
  var parameters =  Map[Symbol, Any]()
  parameters += ('mongoHost -> "10.208.9.118")
  parameters += ('mongoPort -> 27018)
  parameters += ('mongoDB -> "RNA")
  parameters += ('mongoCollection -> "base")
  parameters += ('mongoOutDB -> "CPPlus")
  parameters += ('mongoOutCollection -> "ents")

  var parmSet1 =  Map[Symbol, Any]()
  parmSet1 += ('name -> "Terry")
  var parmSet2 =  Map[Symbol, Any]()
  parmSet2 += ('name -> "Heinze")



  //Create a basic pipeline:
  // - pass a reference to this app if a component needs access to the parameter list (usual behavior)
  // - add the component or component factories to the basic pipeline, in order of execution
  val pipeline = new BasicPipeline
//  pipeline ++ MongoReader(parameters)
//  pipeline ++ RADTokenizerSentenceSplitter
//  pipeline ++ Annotator(parmSet1)
//  pipeline ++ Annotator(parmSet2)
//  pipeline ++ MongoWriter(parameters)




  //Get a list of RNA records:
  // - query by a date range
  // - return a list of mongo object ids
  val oids = getRecords("2010-04-30 12:00:00", "2010-04-30 12:01:00") //166  records

  val startTime = System.nanoTime()

  //For each record, create a CAS
  // - add to the CAS's metadata field, a JSON object that has the object id
  // - pass the CAS to the basic pipeline, which will excute the process function on the component
  var cas: CAS = _
  oids.foreach(oid => {
    println(oid)
    import com.mongodb.casbah.commons.MongoDBObject
    cas = new CAS()
    val mo = MongoDBObject("OID" -> oid)
    cas.metadata = mo.toString
    pipeline run cas
  })



  val elapsedTime = System.nanoTime() - startTime
  println(elapsedTime / 1000000.0 + " ms")



  //  casPool.foreach(cas => cas('text).getAll('root).foreach(annot => println("annot ---------> " + annot.text + "  " + annot.name)))

  //End of pipeline


  //************************************************** functions
  def getRecords(start: String, end: String): List[String] = {
    RNAMongoStorage.name = "RNA"
    RNAMongoStorage.collection = "base"
    RNAMongoStorage.host = "U0069724-MACB.local"
//    RNAMongoStorage.host = "127.0.0.1"
    RNAMongoStorage.port = 27017

    RNAMongoStorage.connect

    val oids = RNAMongoStorage.query("2010-04-30 12:00:00", "2010-04-30 12:01:00") //166  records

    RNAMongoStorage.disconnect

    oids
  }

}

