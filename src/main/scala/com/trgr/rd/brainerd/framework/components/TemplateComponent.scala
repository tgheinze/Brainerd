package com.trgr.rd.nlp.framework.component

import com.trgr.rd.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */





//******************************************************
class TemplateComponent(var parms: Map[Symbol, Any]) extends Component {
  def this() = this(null)



  def process(c: CAS): Unit = {

  }
}

object TemplateComponent extends ComponentFactory {
  def apply() = new TemplateComponent()
  def apply(p: Map[Symbol, Any]) = new TemplateComponent(p)
}
//******************************************************








