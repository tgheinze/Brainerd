package org.heinze.brainerd.framework.util

import org.heinze.brainerd.framework.artifacts.CAS


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/1/12
 * Time: 5:55 PM
 */

class PooledPipeline {

  import org.heinze.brainerd.framework.components.ComponentFactory


  var poolSequence: List[LoaderPool] = Nil


  def ++(t: (ComponentFactory, Int)) = {

    val clp = ComponentFactoryLoaderPool(t._2, t._1)
    if (poolSequence.length != 0)
      poolSequence.head.next = clp
    poolSequence = clp :: poolSequence

  }

  def ++(t: (ComponentFactory, Int, Map[Symbol, Any])) = {

    val clp = ComponentFactoryLoaderPool(t._2, t._1, t._3)
    if (poolSequence.length != 0)
      poolSequence.head.next = clp
    poolSequence = clp :: poolSequence

  }

  def run(cas: CAS) = {
    poolSequence.last ! cas
  }

  def terminate() = poolSequence.last ! "Terminate"
}
