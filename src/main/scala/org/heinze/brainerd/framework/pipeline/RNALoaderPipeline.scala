package org.heinze.brainerd.framework.pipeline

import com.weiglewilczek.slf4s.Logging

import org.heinze.brainerd.framework.artifacts.CAS
import org.heinze.brainerd.framework.components.MongoRNALoader
import org.heinze.brainerd.framework.util.{ReadFiles, PooledPipeline}

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 11:54 AM
 */

object RNALoaderPipeline extends App with Logging {


  logger.info("Starting app")

  //Define any global parameters that the components need
  var parameters =  Map[Symbol, Any]()
  parameters += ('mongoHost -> "127.0.0.1")
  parameters += ('mongoPort -> 27017)
  parameters += ('mongoDB -> "RNA")
  parameters += ('mongoCollection -> "base")

//  parameters += ('mongoHost -> "10.208.9.118")
//  parameters += ('mongoPort -> 27018)
//  parameters += ('mongoDB -> "RNA")
//  parameters += ('mongoCollection -> "base18")


  //Create a pooled pipeline:
  // - define each pool of components by adding a tuple with the component factory
  //   and how many component instances to create
  //   and an optional reference to a list of system parameters
  val pipeline = new PooledPipeline
  pipeline ++ (MongoRNALoader, 8, parameters)

  //Get a list of RNA cvs files:
  val files = ReadFiles.getFiles("/Volumes/3TB/RNA/2003")

  var casPool: List[CAS] = Nil

  val startTime = System.nanoTime()

  //For each file, create a CAS

  files.foreach(file => {
    import com.mongodb.casbah.commons.MongoDBObject
    val cas = new CAS()
    val mo = MongoDBObject("file" -> file.getAbsolutePath)
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
    cp.foreach(c => if (c.active) newPool += c)
    //    println("active pool size: " + newPool.size)
    casPool = newPool.toList
    cas match {
      case c: CAS => casPool = cas :: casPool
      case _ =>
    }
  }




}

