package com.trgr.rd.brainerd.framework.components

import com.trgr.rd.brainerd.framework.artifacts.CAS
import com.trgr.rd.nlp.framework.component.{ComponentFactory, Component}

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */



class CVSReader(var parms: Map[Symbol, Any]) extends Component {

  def this() = this(null)



  def process(c: CAS): Unit = {

  }
}

object CVSReader extends ComponentFactory {
  def apply() = new CVSReader()
  def apply(parms: Map[Symbol, Any]) = new CVSReader(parms)

}
