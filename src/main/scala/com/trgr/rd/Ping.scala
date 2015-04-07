package com.trgr.rd

import collection.mutable
import scala.Predef._

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/21/12
 * Time: 3:02 PM
 */
object Ping extends App {
  println("com.trgr.rd.Ping")

  val f = """User-level-Message-Report-From-2010-09-26-to-2010-09-27-19-15-01.csv"""

  val regex = """(.+?)(From\-)(.+?)(\-to)(.+?)""".r
//  val regex = """(From\-)(.+?)(\-to)""".r

  val regex(pre, x, value, y, suf) = f  //"From-2010-09-26-to"

  println(pre)
  println(value)
  println(suf)

  val key = "key1"
  val v = "123.45".toDouble

  import scala.collection.mutable.Map
  val myMap = Map[String, Double]()
  val myMap2 = Map[String, Double]()

  myMap += (key -> v)


  println(myMap.get("key1"))
  println(myMap.get("key1").get)




//
//  val xl2 = xl.map(_ * 2)
//
//  println(xl2)
//
//  val m = Map[Int, String]()
//
//  val xl3 = xl.map(myfn(_) )
//
//  val xl4 = xl.map(t => {
//    println(t)
//    t*3
//  } )
//
//  println(xl3)
//  println(xl4)
//
//  println(m)


  val xl = List(
    "one" -> 1.0,
    "two" -> 2.0,
    "three" -> 3.0,
    "four" -> 4.0,
    "five" -> 5.0
  )

  val ml = xl.map(t => {
      myMap2 += t._1 -> t._2
      "asdas" -> 3.33
  }
  )

  val xfn: (String, Int) => Int = fn

  println(xfn("3", 13))

  def fn(s: String, i: Int) = s.toInt + i

  println(ml)
  println(myMap2)


//  def myfn(i: Int): Int = {
//    m += i -> "sdsf"
//    i*2
//  }
//
//  def myfn2(i: Int): Unit = {
//    m += i -> "sdsf"
//    i*2
//  }
}
