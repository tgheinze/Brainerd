package org.heinze.brainerd.framework.components

import org.heinze.brainerd.framework.artifacts.CAS


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
