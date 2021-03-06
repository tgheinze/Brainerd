package org.heinze.brainerd.framework.util

import org.heinze.brainerd.framework.components.Component
import actors.Actor
import com.weiglewilczek.slf4s._
import org.heinze.brainerd.framework.artifacts.CAS


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/13/12
 * Time: 3:34 PM
 */

class ComponentLoader(val id: Int, val lp: LoaderPool, val component: Component) extends Actor with Logging {

  logger.info("component " + id + " ready to use")
  var ready = true



  def act() {
    react {


      case c: CAS => {
        process(c)
        lp ! (this, c)
        act()
      }

      case "Terminate" => {
        logger.info(id + " Terminating")
        component.finalize()
        exit()
      }

    }
  }


  def process(cas: CAS) {

    //logger.info("loader " + id + " processing CAS with text " + input('text).getText())
    //val startTime = System.nanoTime()

    component.process(cas)


  }





}
