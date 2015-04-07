package com.trgr.rd.brainerd.framework.util


import com.trgr.rd.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/1/12
 * Time: 5:55 PM
 */

class BasicPipeline {

  import com.trgr.rd.nlp.framework.component.{ComponentFactory, Component}




  var componentSequence: List[Component] = Nil

  def ++(component: ComponentFactory) = {
    componentSequence = component() :: componentSequence
  }


  def ++(component: Component) = {
    componentSequence = component :: componentSequence
  }

  def run(cas: CAS) = componentSequence.reverse.foreach(component => component.process(cas))

}
