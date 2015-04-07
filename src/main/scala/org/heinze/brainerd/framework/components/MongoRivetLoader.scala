package org.heinze.brainerd.framework.components

import org.heinze.brainerd.framework.artifacts.CAS

import com.mongodb.casbah.commons.MongoDBObject
import java.io.{FileReader, File}
import au.com.bytecode.opencsv.CSVReader
import java.util.{TimeZone, Date}
import java.text.SimpleDateFormat
import org.heinze.brainerd.framework.util.JSONHelper

import collection.mutable
import com.mongodb



/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/31/12
 * Time: 12:56 PM
 */


class MongoRivetLoader(var parms: Map[Symbol, Any]) extends Component {

  import com.mongodb.casbah.{MongoCollection, MongoDB, MongoConnection}

  def this() = {
    this(null)
  }


  var mongoConn: MongoConnection = _
  var mongoDB: MongoDB = _
  var mongoColl: MongoCollection = _
  var host: String = _
  var port: Int = _
  var db: String = _
  var collection: String = _

  val formDate = new SimpleDateFormat("yyyy-MM-dd z")
  formDate.setTimeZone(TimeZone.getTimeZone("GMT"))

  val formTime = new SimpleDateFormat("hh:mm:ss.SSS z")
  formTime.setTimeZone(TimeZone.getTimeZone("GMT"))

  val formDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z")
  formDateTime.setTimeZone(TimeZone.getTimeZone("GMT"))



  parms match {
    case p: Map[Symbol, Any] => {
      host = p.get('mongoHost).get.asInstanceOf[String]
      port = p.get('mongoPort).get.asInstanceOf[Int]
      db = p.get('mongoDB).get.asInstanceOf[String]
      collection = p.get('mongoCollection).get.asInstanceOf[String]

    }
    case _ => {
      logger.info("no parameter object passed to component MongoRivetLoader")
    }
  }



  connect
  createIndex


  def process(c: CAS): Unit = {

    val file = JSONHelper.extractFile(c.metadata)

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
      mongoColl.ensureIndex(MongoDBObject("date" -> 1), "date", false)
      mongoColl.ensureIndex(MongoDBObject("Email From" -> 1), "emailFrom", false)
      mongoColl.ensureIndex(MongoDBObject("Email To" -> 1), "emailTo", false)
      mongoColl.ensureIndex(MongoDBObject("Messages" -> 1), "messages", false)
    }
    catch {
      case e: Exception => logger.error(e.getStackTrace.toString)
    }

  }



  def load(input: File) {

    println("loading " + input.getAbsolutePath)

    val pd: Date = formDate.parse(getDate(input.getAbsolutePath))


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
        if (count != 0) {
          if (row != null) {
            val doc = MongoDBObject.newBuilder
            var docOk = true
            var lastIndex: Int = 0
            try {
              doc += "date" -> pd
              for (i <- 0 to row.size - 1) {
                lastIndex = i
                val colName = indexMap.get(i.toString).get
                val colValue = row(i)
                colName match {
                  case "Messages" => {
                    doc += colName -> colValue.toInt
                  }

                  case _ => doc += colName -> colValue
                }
              }
            }
            catch {
              case e: Exception => {
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
        if (count % 10000 == 0) {
          println(count + ": elapsed time = " + (System.nanoTime() - startTime) / 1000000000)
        }

      } catch {
        case ex: Exception => {
          println(ex)
          println(insert)
        }
      }

    }
    println(count + ": total time = " + (System.nanoTime() - startTime) / 1000000000)
  }


  def loadHeader(input: File): (List[String], mutable.Map[String, String], mutable.Map[String, String]) = {

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
      case e: Exception => {
        e.printStackTrace(); logger.error(e.toString)
      }
    }
  }

  def getDate(fileName: String)  = {

    val regex = """(.+?)(From\-)(.+?)(\-to)(.+?)""".r
    val regex(pre, x, value, y, suf) = fileName

    val d = value + " GMT"
    d
  }

}


object MongoRivetLoader extends ComponentFactory {
  def apply() =  new MongoRivetLoader()
  def apply(parms: Map[Symbol, Any]) = new MongoRivetLoader(parms)
}

