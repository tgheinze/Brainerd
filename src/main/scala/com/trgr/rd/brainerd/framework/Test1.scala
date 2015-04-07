package com.trgr.rd.nlp.framework

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/30/12
 * Time: 11:14 AM
 */

object Test1 extends App {

  import scala.collection.mutable.ListBuffer

  val x = List(1,2,3,4,5,6)

  println(sum(x))
  println(sum2(x))

  var tokens: List[Token] = Nil

  tokens = new Token(0,10) :: tokens
  tokens = new Token(12,15) :: tokens
  tokens = new Token(16,23) :: tokens
  tokens = new Token(23,-1) :: tokens
  tokens = new Token(24,29) :: tokens
  tokens = new Token(31,37) :: tokens
  tokens = new Token(38,46) :: tokens
  tokens = new Token(47,55) :: tokens
  tokens = new Token(56,-1) :: tokens
  tokens = new Token(56,-1) :: tokens
  tokens = new Token(57,66) :: tokens
  tokens = new Token(66,-1) :: tokens
  tokens = new Token(66,-1) :: tokens


  tokens.reverse.foreach(t => println(t.b))


  val sentences = fold(tokens.reverse)
  println(sentences)


  def trim(x:List[(Int, Int)]): List[(Int, Int)] = {
    if(x.last._2 == -1) {
      trim(x.dropRight(1))
    }
    else
      x
  }


  def fold(x: List[Token]): List[(Int, Int)] = {
    var sentences: List[(Int,Int)] = (0,-1) :: Nil

    val res = (sentences /: x) (mergeOp(_,_))

    trim(res)
  }

  def mergeOp(a:List[(Int,Int)], b:Token): List[(Int,Int)] = {



    if(b.e == -1) {
      //terminator Token
      if (a.last._2 == -1)
        a
      else {
        val last = (b.b, -1)
        val newList = a ::: last :: Nil
        newList
      }
    } else {
      val last = (a.last._1, b.e)
      val newList = a.dropRight(1) ::: last :: Nil
      newList
    }

  }





  def sum(x: List[Int]): Int = {
    (0 /: x) (_ + _)
  }

  def sum2(x: List[Int]): Int = {
    (0 /: x) (op1(_,_))
  }

  def op1(a:Int, b:Int): Int = a+b
}

class Token(val b: Int, val e: Int)
