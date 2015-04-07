package org.heinze.brainerd.framework.components

import org.heinze.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */




class Annotator(var parms: Map[Symbol, Any]) extends Component {

  def this() = this(null)



  def process(c: CAS): Unit = {
      println(parms.get('name))



  }
}

object Annotator extends ComponentFactory {
  def apply() = new Annotator()
  def apply(p: Map[Symbol, Any]) = new Annotator(p)
}

