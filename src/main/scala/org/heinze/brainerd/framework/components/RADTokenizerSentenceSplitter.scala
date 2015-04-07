package org.heinze.brainerd.framework.components

import org.heinze.brainerd.framework.artifacts.CAS

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */



class RADTokenizerSentenceSplitter(var parms: Map[Symbol, Any]) extends Component {


  import com.tr.research.util.tokenizer.Token

  def this() = this(null)


  def process(c: CAS): Unit = {

    //Define the types that are created by this component
    c('text).addRelation('root -> 'Doc)
    c('text).addRelation('Doc -> 'Sent)
    c('text).addRelation('Sent -> 'Token)


    if(c('text).getText().size > 0)  {
      //tokenize and save
      val rdtokens = tokenize(c('text).getText())
      rdtokens.foreach(token => {
        if (token.endOffset >= 0) {
          val b = token.getStartOffset
          val e = token.getEndOffset + 1
          c('text)('Token, b, e)
        }
      })


      //Mark the sentence boundaries and save
      getSentences(rdtokens.toList).foreach(s => c('text)('Sent, s._1, s._2))

    }


  }

  //RD Tokenizer
  def tokenize(text: String): Array[Token] = {
    import com.tr.research.util.tokenizer.Tokenizer2
    val tokenizer = new Tokenizer2(text, new Array[Int](0));
    tokenizer.getTokens()
  }


  //RD Sentence splitter
  def getSentences(tokens: List[Token]): List[(Int, Int)] = {
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

object RADTokenizerSentenceSplitter extends ComponentFactory {
  def apply() = new RADTokenizerSentenceSplitter()
  def apply(parms: Map[Symbol, Any]) = new RADTokenizerSentenceSplitter(parms)
}

