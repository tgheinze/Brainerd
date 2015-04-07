package com.trgr.rd.brainerd.framework.artifacts

class Annotation(val name: Symbol, val begin: Int, val end: Int, val view: View) extends Span {
  var meta: String = _
  lazy val text = coveredText(begin, end, view.getText())
}

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/26/12
 * Time: 1:55 PM
 */
trait Span {
  def coveredText(begin: Int, end: Int, text: String) = text.substring(begin, end)
}