package com.trgr.rd.nlp.framework

import com.trgr.rd.brainerd.framework.artifacts.CAS


object TestThis2 extends App {

  import com.tr.research.util.tokenizer.Token
  import scala.Array

  val cas = new CAS()
  val textView = cas('text)
  val docView = cas('doc)

  //Define relationships
  textView.addRelation('root -> 'Person)
  textView.addRelation('root -> 'Org)
  textView.addRelation('Person -> 'Female)
  textView.addRelation('Person -> 'Male)
  textView.addRelation('Org -> 'Comp)
  textView.addRelation('Org -> 'Edu)
  textView.addRelation('Edu -> 'Public)
  textView.addRelation('Edu -> 'Private)

  textView.addRelation('docRoot -> 'Para)
  textView.addRelation('Para -> 'Sent)
  textView.addRelation('Sent -> 'Token)

  //Show some relationships
  println(textView.getRelationships('Public))
  println(textView.getRelationships('Edu))
  println(textView.getRelationships('Org))
  println(textView.getRelationships('root))

  println(textView.getRelationships('docRoot))

  //set some text
  textView.setText(
    """Computers know none of these things, so any program that attempts to extract information from text must somehow be equipped with the tools for doing so. One method is to supply the computer with rules that say things like "if you find a sequence of initially capitalized words followed by 'Inc.', you have found the name of a company." This isn't a fool-proof rule, because it will miss some company names (e.g., "eBay", or any that don't use "Inc.") and find other things that are not company names, e.g., the movie title "Monsters, Inc." But the rule will be right a lot of the time, and find many company names. And you can add more rules, make existing rules more complicated, prioritize or weight rules, etc. Typically, a domain expert creates or edits these rules by hand. Writing and tweaking rules is a lot of work, but the effort can be worthwhile. A program called a rule interpreter then applies these rules as it reads text word by word, and performs the extractions allowed by the rules.
    """.stripMargin
  )

  //Create some annotations
  textView('Person, 10, 20)
  textView('Person, 33, 35)
  textView('Person, 78, 90)

  textView('Male, 178, 190)
  textView('Male, 199, 213)
  textView('Female, 123, 135)
  textView('Female, 155, 166)

  textView('Public, 1, 2)
  textView('Public, 3, 4)
  textView('Public, 5, 6)

  textView('Private, 11, 21)
  textView('Private, 31, 41)
  textView('Private, 51, 61)

  val c1 = textView('Comp, 100, 111)

  //show some indices

  println('Female)
  textView.get('Female).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end))

  println('Private)
  textView.get('Private).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end))

  println('Edu)
  textView.get('Edu).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end))

  println('root)
  textView.getAll('root).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end))



  println('Comp)
  textView.get('Comp).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end + " with metadata: " + f.meta))
  c1.meta = """{id:"uuid", text:"Thomson Reuters", ric:"TRI", relatedRICs: ["AAA", "BBB", "CCC"]} """
  println('Comp + " after metadata add")
  textView.get('Comp).foreach(f => println(f.name + " begins at " + f.begin + " and ends at " + f.end + " with metadata: " + f.meta))

  //Sorted example
  println('root + " sorted")
  textView.getAllSorted('root).foreach(annot => println(annot._1 + " -> " + annot._2.name + "   spanned text: " + annot._2.text))


  //sub-iterator text
  println("\nsub-iterator")
  val span = 75 -> 160
  for (annot <- textView.getAllSorted('root)
       if annot._1 >= span._1
       if annot._1 <= span._2) {
    println(annot._1 + " -> " + annot._2.name + "   spanned text: " + annot._2.text)
  }

  //fake tokenizer
  var offset = 0
  val tokens = textView.getText().split(" ")
  tokens.foreach(token => {
    val b = offset
    val e = b + token.length
    offset = e + 1
    textView('Token, b, e)
  })


  println("\nthe fake tokens, sorted")
  textView.getSorted('Token).foreach(annot => println(annot._1 + " -> " + annot._2.name + "   spanned text: " + annot._2.text))

  textView.addRelation('Sent -> 'RDToken)

  val rdtokens = tokenize(textView.getText())
  rdtokens.foreach(token => {
    if (token.endOffset >= 0) {
      val b = token.getStartOffset
      val e = token.getEndOffset + 1
      textView('RDToken, b, e)
    }
  })

  println("\nreal tokens, sorted")
  textView.getSorted('RDToken).foreach(annot => println(annot._1 + " -> " + annot._2.name + "   spanned text: " + annot._2.text))


  getSentencesLF(rdtokens.toList).foreach(s => textView('Sent, s._1, s._2))


  println("\nsentences, sorted")
  textView.getSorted('Sent).foreach(annot => println(annot._1 + " -> " + annot._2.name + "   spanned text: " + annot._2.text))




  //test functions

  //RD Tokenizer

  import com.tr.research.util.tokenizer.Tokenizer2

  def tokenize(text: String): Array[Token] = {
    val tokenizer = new Tokenizer2(text, new Array[Int](0));
    tokenizer.getTokens()
  }


  //RD Sentence splitter
  def getSentences(tokens: List[Token]): List[(Int, Int)] = {
    import scala.collection.mutable.ListBuffer

    val offsets = ListBuffer[(Int, Int)]()
    var b: Int = 0
    var e: Int = 0

    for (token <- tokens) {
      //       println(token.getStartOffset + "," + token.getEndOffset)
      if (token.getEndOffset == -1) {
        //sentence boundary
        if (b != -1)
          offsets += b -> (e + 1)
        b = -1
      } else {
        if (b == -1) b = token.getStartOffset
        e = token.getEndOffset
      }
    }

    offsets.toList
  }

  def getSentencesLF(tokens: List[Token]): List[(Int, Int)] = {
    var sentences: List[(Int, Int)] = (0, -1) :: Nil

    val res = (sentences /: tokens)(mergeOp(_, _))

    trim(res)
  }

  def trim(x: List[(Int, Int)]): List[(Int, Int)] = {
    if (x.last._2 == -1) {
      trim(x.dropRight(1))
    }
    else
      x
  }


  def mergeOp(a: List[(Int, Int)], b: Token): List[(Int, Int)] = {

    if (b.getEndOffset == -1) {
      //terminator Token
      if (a.last._2 == -1) //skip multiple s-breaks
        a
      else {
        val last = (b.getStartOffset, -1)
        val newList = a ::: last :: Nil
        newList
      }
    } else {
      val last = (a.last._1, b.getEndOffset + 1)
      val newList = a.dropRight(1) ::: last :: Nil
      newList
    }

  }

}

