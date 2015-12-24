
import scala.util.{ Failure, Success }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.io.Source._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo._
import spray.json._

/**
 * Case class representing a sensor
 */
case class Sensor(
  battery: Int,
  controller: String,
  humidity: Int,
  location: String,
  luminance: Int,
  motion: Boolean,
  sensor: Int,
  temperature: Double,
  updateTime: Long)

    //def this(m: Map[String, Any]) {
    //  this(m.get("battery").asInstanceOf[Int], m.get("controller").asInstanceOf[String],
    //    m.get("humidity").asInstanceOf[Int], m.get("location").asInstanceOf[String],
    //    m.get("luminance").asInstanceOf[Int], m.get("motion").asInstanceOf[Boolean],
    //    m.get("sensor").asInstanceOf[Int], m.get("temperature").asInstanceOf[Int],
    //    m.get("updateTime").asInstanceOf[Int])
    //}

object Sensor {
  implicit object SensorWriter extends BSONDocumentWriter[Sensor] {
    def write(sensor: Sensor): BSONDocument = BSONDocument(
      "battery" -> sensor.battery,
      "controller" -> sensor.controller,
      "humidity" -> sensor.humidity,
      "location" -> sensor.location,
      "luminance" -> sensor.luminance,
      "motion" -> sensor.motion,
      "sensor" -> sensor.sensor,
      "temperature" -> sensor.temperature,
      "updateTime" -> sensor.updateTime
    )
  }

  implicit object SensorReader extends BSONDocumentReader[Sensor] {
    def read(doc: BSONDocument): Sensor = Sensor(
      doc.getAs[Int]("battery").get,
      doc.getAs[String]("controller").get,
      doc.getAs[Int]("humidity").get,
      doc.getAs[String]("location").get,
      doc.getAs[Int]("luminance").get,
      doc.getAs[Boolean]("motion").get,
      doc.getAs[Int]("sensor").get,
      doc.getAs[Double]("temperature").get,
      doc.getAs[Long]("updateTime").get
    )
  }
}


object Client {

  private final val urlAll = "http://129.194.185.199:5000/sensors/3/all_measures"
  // Database connection
  val driver = new MongoDriver
  val connection = driver.connection(List("86.119.33.122:27017"))

  val db = connection("SmarthepiaDB")
  val collection = db.collection("sensors")
  //val db = connection("t")
  //val collection = db.collection("test")

  def get(url: String) = scala.io.Source.fromURL(url).mkString
  //def get(url: String) = scala.io.Source.fromURL(url).getLines.toList


  def main(args: Array[String]): Unit = {
    val data = get(urlAll)
    println(data)
    val m = scala.util.parsing.json.JSON.parseFull(data)
    println(m)
    //val sensor = new Sensor(this(m.get("battery").asInstanceOf[Int], m.get("controller").asInstanceOf[String],
    //    m.get("humidity").asInstanceOf[Int], m.get("location").asInstanceOf[String],
    //    m.get("luminance").asInstanceOf[Int], m.get("motion").asInstanceOf[Boolean],
    //    m.get("sensor").asInstanceOf[Int], m.get("temperature").asInstanceOf[Int],
    //    m.get("updateTime").asInstanceOf[Int])
    //)
    val sensor = Sensor(100, "Pi 4", 21, "A401", 52, false, 3, 24.0, 1450956999)
    println(sensor)
    val document = BSON.write(sensor)
    val s = BSON.readDocument[Sensor](document)
    println(s)

    collection.insert(document)
    //println(document)
    //val document = BSONDocument(sensor)
    //val document = BSON.writeDocument(sensor)
    //println(data(1))
    //val m = Map(data map {_.
    //println(j)

    //val sensor = Sensor(data)
    //println(sensor)

    //val document = BSONDocument(
    //  "firstName" -> "Stephaneeee",
    //  "lastName" -> "Godbillon",
    //  "age" -> 29)

    println("before insert")
    val future = collection.insert(document)
    println("after insert")
    //val future = collection.insert(jValue)

    future.onComplete {
      case Failure(e) => throw e
      case Success(lastError) => {
        println("successfully inserted document with lastError = " + lastError)
      }
    }
    //collection.insert(document).map(lastError =>
    //    println("Mongo LastError: %s".format(lastError)))
    sys.exit(0)
  }
}
