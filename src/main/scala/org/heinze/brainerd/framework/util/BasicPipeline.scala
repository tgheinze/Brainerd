package org.heinze.brainerd.framework.util

import org.heinze.brainerd.framework.artifacts.CAS
import org.heinze.brainerd.framework.components._

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/1/12
 * Time: 5:55 PM
 */

class BasicPipeline {





  var componentSequence: List[Component] = Nil

  def ++(component: ComponentFactory) = {
    componentSequence = component() :: componentSequence
  }


  def ++(component: Component) = {
    componentSequence = component :: componentSequence
  }

  def run(cas: CAS) = componentSequence.reverse.foreach(component => component.process(cas))

}
