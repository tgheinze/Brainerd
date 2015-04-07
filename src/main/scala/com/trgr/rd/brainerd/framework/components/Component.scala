package com.trgr.rd.nlp.framework.component

import com.weiglewilczek.slf4s.Logging
import com.trgr.rd.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:51 PM
 */

trait Component extends Logging {

  def process(c: CAS): Unit
  override def finalize() = {}
}

trait ComponentFactory  {
  def apply(): Component
  def apply(p: Map[Symbol, Any]): Component
}





