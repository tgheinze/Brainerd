package com.trgr.rd.brainerd.framework.components

import com.trgr.rd.brainerd.framework.artifacts.CAS
import com.trgr.rd.nlp.framework.component.{ComponentFactory, Component}
import com.mongodb.casbah.commons.MongoDBObject
import java.io.{FileReader, File}
import au.com.bytecode.opencsv.CSVReader
import java.util.{TimeZone, Date}
import java.text.SimpleDateFormat
import collection.mutable
import java.util
import com.mongodb
import com.trgr.rd.nlp.framework.util.JSONHelper


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */



class MongoRNALoader(var parms: Map[Symbol, Any]) extends Component {

  import com.mongodb.casbah.{MongoCollection, MongoDB, MongoConnection}

  def this() = {
    this(null)
    println("Huh????")
  }


  var mongoConn: MongoConnection = _
  var mongoDB: MongoDB = _
  var mongoColl: MongoCollection = _
  var host: String = _
  var port: Int = _
  var db: String = _
  var collection: String = _

  val formDate = new SimpleDateFormat("yyyy-MM-dd z")
  formDate.setTimeZone(TimeZone.getTimeZone("GMT"));

  val formTime = new SimpleDateFormat("hh:mm:ss.SSS z")
  formTime.setTimeZone(TimeZone.getTimeZone("GMT"));

  val formDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z")
  formDateTime.setTimeZone(TimeZone.getTimeZone("GMT"));



  parms match {
    case p: Map[Symbol, Any] => {
      host = p.get('mongoHost).get.asInstanceOf[String]
      port = p.get('mongoPort).get.asInstanceOf[Int]
      db = p.get('mongoDB).get.asInstanceOf[String]
      collection = p.get('mongoCollection).get.asInstanceOf[String]
      println("MongoRNALoader INSTANCE 2")
    }
    case _ =>   {
      println("MongoRNALoader INSTANCE 1")
    }
  }



  connect
  createIndex


  def process(c: CAS): Unit = {

    val file = JSONHelper.extractFile(c.metadata)
    println(file)
    try {
      load(new File(file))
    }
    catch {
      case e: Exception => logger.error(e.toString)
    }

  }
  override def finalize() = {
    logger.info("cleaning up...")
    disconnect
  }
  def connect() {
    try {
      import com.mongodb.casbah.MongoConnection
      mongoConn = MongoConnection(host, port)
      mongoDB = mongoConn(db)
      mongoColl = mongoDB(collection)
      println("...")

    } catch {
      case e: Exception => logger.error("Unable to connect to storage")
    }
  }

  def disconnect {
    try {
      mongoConn.close()
    } catch {
      case e: Exception => println("Unable to disconnect from storage")
    }
  }


  def createIndex() {

    try {
      mongoColl.ensureIndex(MongoDBObject("STORY_DATE_TIME" -> 1), "story_date_time_key", true)
    }
    catch {
      case e: Exception => logger.error(e.getStackTrace.toString)
    }

  }

//  def extractFile(file: String): String = {
//    import net.liftweb.json._
//    implicit val formats = DefaultFormats
//    case class Meta(file: String)
//    val meta: _root_.net.liftweb.json.JValue = parse(file)
//    val md = meta.extract[Meta]
//    md.file
//  }

  def load(input: File) {

    println("loading " + input.getAbsolutePath)
    val startTime = System.nanoTime()

    val headerInfo = loadHeader(input)
    var header: List[String] = headerInfo._1
    var columnMap = headerInfo._2
    var indexMap = headerInfo._3


    val reader = new CSVReader(new FileReader(input))
    var count = 0
    var hasMore = true



    while (hasMore) {

      var insert = ""

      try {
        val row = reader.readNext()
        if (row == null)
          hasMore = false
        if (count != 0)  {
          if (row != null)  {
            val doc = MongoDBObject.newBuilder
            var docOk = true
            var lastIndex: Int = 0
            try {
              for (i <- 0 to row.size - 1) {
                lastIndex = i
                val colName = indexMap.get(i.toString).get
                val colValue = row(i)

                colName match {
                  case "DATE" => {
                    val aDate = colValue + " GMT"
                    try {
                      val pd: Date = formDate.parse(aDate)
                      doc += colName -> pd
                    }
                    catch {
                      case e: Exception => doc += colName -> colValue
                    }
                  }
                  case "STORY_DATE_TIME" => {
                    val aDate = colValue + " GMT"
                    try {
                      val pd: Date = formDateTime.parse(aDate)
                      doc += colName -> pd
                    }
                    catch {
                      case e: Exception => doc += colName -> colValue
                    }
                  }
                  case "TAKE_DATE_TIME" => {
                    val aDate = colValue + " GMT"
                    try {
                      val pd: Date = formDateTime.parse(aDate)
                      doc += colName -> pd
                    }
                    catch {
                      case e: Exception => doc += colName -> colValue
                    }
                  }
                  case _ => doc += colName -> colValue
                }

              }
            }
            catch {
              case e: Exception => {
                //skip non-conforming articles: lists and non-English
                //                println("row size: " + row.size + "   bad index: " + lastIndex)
                //                for (i <- 0 to row.size - 1) {
                //                  println("column " + i + ":  ---->  " + row(i))
                //                }
                docOk = false
              }
            }

            if (docOk) {
              val mdoc = doc.result()
              val res = mongoColl += mdoc
              checkForDuplicate(res)
            }

          } else {
            hasMore = false
          }
        }

        count += 1
        if (count % 10000 == 0)  {
          println(count + ": elapsed time = " + (System.nanoTime() - startTime) / 1000000000)
        }

      } catch {
        case ex: Exception =>  {println(ex)
          println(insert)}
      }

    }
    println(count + ": total time = " + (System.nanoTime() - startTime) / 1000000000)
  }



  def loadHeader(input: File): (List[String], mutable.Map[String, String], mutable.Map[String, String]) =  {

    var columnMap = mutable.Map.empty[String, String]
    var indexMap = mutable.Map.empty[String, String]


    val reader = new CSVReader(new FileReader(input))
    var h = reader.readNext
    reader.close()

    val header = h.toList.map(i => i.replaceAll( """\.""", "_"))

    for (i <- 0 to header.size - 1) {
    val index = i.toString
    var name = header(i)
    columnMap.put(name, index)
    indexMap.put(index, name)
    //println(name)
    }
    println("Number of columns in header: " + header.size)

    (header, columnMap, indexMap)
  }

  def checkForDuplicate(res: mongodb.WriteResult) = {
    try {
      val err = res.getError
      if (err != null)
        logger.debug(err)
    }
    catch {
      case e: Exception => {e.printStackTrace(); logger.error(e.toString)}
    }
  }

}

object MongoRNALoader extends ComponentFactory {
  def apply() =  new MongoRNALoader()
  def apply(parms: Map[Symbol, Any]) = new MongoRNALoader(parms)
}

