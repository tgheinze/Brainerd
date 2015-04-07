package org.heinze.brainerd.framework.util

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/1/12
 * Time: 4:37 PM
 */

object JSONHelper {
  def extractOID(oid: String): String = {
    import net.liftweb.json._
    implicit val formats = DefaultFormats
    case class Meta(OID: String)
    val meta = parse(oid)
    val md = meta.extract[Meta]
    md.OID
  }

  def extractFile(file: String): String = {
    import net.liftweb.json._
    implicit val formats = DefaultFormats
    case class Meta(file: String)
    val meta: _root_.net.liftweb.json.JValue = parse(file)
    val md = meta.extract[Meta]
    md.file
  }
}
