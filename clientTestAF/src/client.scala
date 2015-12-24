import scala.util.{ Failure, Success }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.parsing.json._

import scala.io.Source._
import reactivemongo.api._
import reactivemongo.api.MongoDriver
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import reactivemongo.bson.BSONDocument
import reactivemongo._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

import java.util.Timer
import java.time.Duration


/**
 * @brief Case class representing a sensor
 *
 * Did not manage to keep Int type, had to use Double
 */
case class Sensor(
  battery: Double,
  controller: String,
  humidity: Double,
  location: String,
  luminance: Double,
  motion: Boolean,
  sensor: Double,
  temperature: Double,
  updateTime: Double)

/**
 * @brief Sensor object to write into BSON
 */
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


/**
 * @brief REST client
 */
object Client {

  private final val urlAll = "http://129.194.185.199:5000/sensors/3/all_measures"
  // Database connection
  val driver = new MongoDriver
  val connection = driver.connection(List("86.119.33.122:27017"))

  val db = connection("SmarthepiaDB")
  val collection = db.collection("sensors")

  /**
   * @brief HTTP request on the REST server (raspberry) to get information from sensors
   *
   * @param url Url of the server
   *
   * @return A string contening the data
   */
  def get(url: String) = scala.io.Source.fromURL(url).mkString


  /**
   * @brief Main function of the client
   */
  def main(args: Array[String]): Unit = {
    process()
    //Scheduler.schedule(() => println("Do something"), 0L, 5L, TimeUnit.SECONDS)
    //val system = akka.actor.ActorSystem("system")
    //system.scheduler.schedule(0 seconds, 5 seconds){
    //  println("Do something")
    //}
    //system.scheduler.schedule(0 seconds, 5 seconds)(process())

    //object task extends TimerTask {
    //  var isRunning = false

    //  def run() {
    //    if (!isRunning) {
    //      isRunning = true
    //      process()
    //      isRunning = false
    //    }
    //  }
    //}

    //new Timer().schedule(task, 0, 1000)
    //TimerTask task = new TimerSchedulePeriod();
    //Timer timer = new Timer();
    //timer.schedule(task, 100, 100);

    //public void run() {
    //  println("Timer");
    //}
  }

  /**
   * @brief Download the data and write them into MongoDB
   *
   * This function is called repeatedly
   *
   * @return
   */
  def process(): Unit = {
    // Get the data from the server
    val data = get(urlAll)

    // Parse the json string and create a Map[String, Any] containing the data
    val json: Option[Any] = JSON.parseFull(data)
    val m: Map[String, Any] = json match {
      case Some(value) => value.asInstanceOf[Map[String, Any]]
      case None => null
    }
    // Creation of the sensor
    val sensor = createSensor(m)
    println(sensor)
    // Creation of the document
    val document = BSON.write(sensor)

    // Insert the document into MongoDB
    val future = collection.insert(document)
    future.onComplete {
      case Failure(e) => throw e
      case Success(lastError) => {
        println("successfully inserted document with lastError = " + lastError)
      }
    }
    // WTF is the app not exiting ???
    //sys.exit(0)
  }

  /**
   * @brief Creation of a sensor
   *
   * Really bad way to create it IMHO...
   * But since I did not manage to do it another way yet...
   *
   * @param data Data in a map
   *
   * @return A new sensor containing data
   */
  def createSensor(data: Map[String, Any]): Sensor = {
    val battery = data.getOrElse("battery", 0).asInstanceOf[Double]
    val controller = data.getOrElse("controller", "").asInstanceOf[String]
    val humidity = data.getOrElse("humidity", 0).asInstanceOf[Double]
    val location = data.getOrElse("location", "").asInstanceOf[String]
    val luminance = data.getOrElse("luminance", 0).asInstanceOf[Double]
    val motion = data.getOrElse("motion", 0).asInstanceOf[Boolean]
    val sensor = data.getOrElse("sensor", 0).asInstanceOf[Double]
    val temperature = data.getOrElse("temperature", 0.0).asInstanceOf[Double]
    val updateTime = data.getOrElse("updateTime", 0).asInstanceOf[Double]

    Sensor(battery, controller, humidity, location, luminance, motion, sensor, temperature, updateTime)
  }
}
