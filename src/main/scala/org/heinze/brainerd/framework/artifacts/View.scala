package org.heinze.brainerd.framework.artifacts

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/26/12
 * Time: 1:55 PM
 */

class CAS {
  //Holds an application specific json object
  var metadata: String = _
  var active = true

  import scala.collection.mutable.Map

  val viewMap: Map[Symbol, View] = Map[Symbol, View]()

  def apply(name: Symbol) = {
    viewMap.get(name) match {
      case None => {
        val v = new View(name)
        viewMap += (name -> v)
        v
      }
      case Some(v) => v
    }

  }
}

object View {

  import scala.collection.mutable.Map

  val viewMap: Map[Symbol, View] = Map[Symbol, View]()

  def apply(name: Symbol) = {
    viewMap.get(name) match {
      case None => {
        val v = new View(name)
        viewMap += (name -> v)
        v
      }
      case Some(v) => v
    }

  }
}

class View(private var name: Symbol = null) {

  import scala.collection.mutable.{ListBuffer, Map}
  import scala.collection.immutable.TreeMap

  //pseudo-CAS
  private val imap = Map[Symbol, Map[Int, Annotation]]()
  private val cmap = Map[Symbol, ListBuffer[Symbol]]()
  private var text: String = _
  //  private var name: Symbol = null


  def apply(annot: Symbol, begin: Int, end: Int) = {
    val a = new Annotation(annot, begin, end, this)
    index(a)
    a
  }

  def getName =
    name

  private def index(annot: Annotation) = {
    if (imap.get(annot.name) == None) {
      imap.put(annot.name, Map[Int, Annotation]())
    }
    val tm = imap.get(annot.name).get
    tm.put(annot.begin, annot)
  }

  def addRelation(pair: (Symbol, Symbol)) = {
    if (cmap.get(pair._1) == None) {
      cmap.put(pair._1, ListBuffer[Symbol]())
    }
    val mp = cmap.get(pair._1).get
    if (!mp.contains(pair._2))
      mp += pair._2

  }

  def get(annot: Symbol): List[Annotation] = {
    val out = ListBuffer[Annotation]()
    val map = imap.get(annot)
    map match {
      case Some(m) => m.foreach(item => out += item._2)
      case None =>
    }

    out.toList
  }

  def getSorted(annot: Symbol): TreeMap[Int, Annotation] = {
    import scala.collection.immutable.TreeMap

    val a = get(annot)
    var tmap = TreeMap[Int, Annotation]()
    a.foreach(a => tmap += a.begin -> a)
    tmap
  }

  def getAll(annot: Symbol): List[Annotation] = {
    val rels = getRelationships(annot)
    var out: List[Annotation] = Nil
    rels.foreach(r => out = get(r) ::: out)
    out
  }

  def getAllSorted(annot: Symbol): TreeMap[Int, Annotation] = {
    import scala.collection.immutable.TreeMap

    val a = getAll(annot)
    var tmap = TreeMap[Int, Annotation]()
    a.foreach(a => tmap += a.begin -> a)
    tmap
  }


  def getRelationships(annot: Symbol): List[Symbol] = {
    var out: List[Symbol] = Nil
    val childList = cmap.get(annot)
    childList match {
      case None => {
        out = annot :: out
      }
      case Some(lb) => {
        out = annot :: out
        lb.foreach(sym => {
          val y: List[Symbol] = getRelationships(sym)
          y match {
            case Nil => out
            case _ => out = y ::: out
          }
        })
      }
    }
    out

  }

  def setText(text: String) = this.text = text

  def getText() = text


  def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

}




