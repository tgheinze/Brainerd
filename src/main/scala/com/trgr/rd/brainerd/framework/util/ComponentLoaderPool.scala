package com.trgr.rd.nlp.framework.util

import actors.Actor
import java.io.File
import com.weiglewilczek.slf4s.Logging
import com.trgr.rd.brainerd.framework.artifacts.CAS
import com.trgr.rd.nlp.framework.component.{ComponentFactory}

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/13/12
 * Time: 3:35 PM
 */

trait LoaderPool extends Actor {
  var next: LoaderPool = null
}

class ComponentFactoryLoaderPool(val poolSize: Int, val factory: ComponentFactory, var parms: Map[Symbol, Any] ) extends Actor with LoaderPool with Logging {

  var queue: List[ComponentLoader] = Nil
  val startTime = System.nanoTime()


  def this(poolSize: Int, factory: ComponentFactory) = {
    this(poolSize, factory, null)
  }



  parms match {
    case p: Map[Symbol, Any] => {
      for (i <- 1 to poolSize) {
        val loader = new ComponentLoader(i, this, factory(p))
        queue = loader :: queue
      }
    }
    case _ => {
      for (i <- 1 to poolSize) {
        val loader = new ComponentLoader(i, this, factory())
        queue = loader :: queue
      }
    }
  }




  queue.foreach(loader => print(loader.id + " -> "))
  println()

  queue.foreach(loader => loader.start())

  logger.info("Starting loaders")



  start()

  def act() {
    react {

      case "Terminate" => {
        logger.info("Terminating")
        queue.foreach(loader => logger.info(loader.id + " -> "))

        queue.foreach(l => l ! "Terminate")

        if (next != null)
          next ! "Terminate"

        exit()
      }

      case (loader: ComponentLoader, cas: CAS) => {
        //logger.info(loader.id + " returning to pool: state = " + loader.ready)

        queue = loader :: queue

        if (next != null)
          next ! cas
        else {
          cas.active = false
        }

        act()
      }

      case c: CAS => {
        if (queue.size > 0) synchronized {
          queue(0) ! c
          queue = queue.drop(1)
        } else {
          //          Thread.sleep(10)
          this ! c
        }
        act()
      }

    }
  }

}

object ComponentFactoryLoaderPool {
  def apply(poolSize: Int, factory: ComponentFactory, parms: Map[Symbol, Any]) =
    new  ComponentFactoryLoaderPool(poolSize, factory, parms)
  def apply(poolSize: Int, factory: ComponentFactory) =
    new  ComponentFactoryLoaderPool(poolSize, factory)
}


//
//class ComponentLoaderPool(val poolSize: Int, val component: Component, var next: LoaderPool) extends Actor with LoaderPool with Logging {
//
//  import com.trgr.rd.nlp.framework.component.Pipeline
//
//  var queue: List[ComponentLoader] = Nil
//  val startTime = System.nanoTime()
//
//
//
//
//  def this(poolSize: Int, component: Component) = {
//    this(poolSize, component, null)
//  }
//
//
//
//
//      for (i <- 1 to poolSize) {
//        val loader = new ComponentLoader(i, this, component)
//        queue = loader :: queue
//      }
//
//
//
//
//
//  queue.foreach(loader => print(loader.id + " -> "))
//  println()
//
//  queue.foreach(loader => loader.start())
//
//  logger.info("Starting loaders")
//
//
//
//  start()
//
//  def act() {
//    react {
//
//      case "Terminate" => {
//        logger.info("Terminating")
//        queue.foreach(loader => logger.info(loader.id + " -> "))
//
//        queue.foreach(l => l ! "Terminate")
//
//        if (next != null)
//          next ! "Terminate"
//
//        exit()
//      }
//
//      case (loader: ComponentLoader, cas: CAS) => {
//        //logger.info(loader.id + " returning to pool: state = " + loader.ready)
//
//        queue = loader :: queue
//
//        if (next != null)
//          next ! cas
//        else {
//          cas.active = false
//        }
//
//        act()
//      }
//
//      case c: CAS => {
//        if (queue.size > 0) synchronized {
//          queue(0) ! c
//          queue = queue.drop(1)
//        } else {
//          //          Thread.sleep(10)
//          this ! c
//        }
//        act()
//      }
//
//    }
//  }
//
//}
//
//object ComponentLoaderPool {
//
//  def apply(poolSize: Int, component: Component, next: LoaderPool) =
//    new  ComponentLoaderPool(poolSize, component, next)
//
//  def apply(poolSize: Int, component: Component) =
//    new  ComponentLoaderPool(poolSize, component)
//}



