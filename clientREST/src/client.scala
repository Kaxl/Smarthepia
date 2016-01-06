import scala.util.{ Failure, Success }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.parsing.json._

// Import for http request
import scala.io.Source._
// Import for ReactiveMongo
import reactivemongo._
import reactivemongo.api._
import reactivemongo.api.MongoDriver
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._

// Import for Quartz library
import java.util.Date

import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Job
import org.quartz.JobExecutionContext

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

  // Database connection
  val driver = new MongoDriver
  val connection = driver.connection(List("86.119.33.122:27017"))
  val db = connection("SmarthepiaDB")
  val collection = db.collection("sensors")

  /**
   * @brief HTTP request on the REST server (raspberry) to get information from sensors
   *
   * Handles timeout if server is not responding
   *
   * @param url Url of the server
   *
   * @return A string contening the data
   */
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def get(url: String,
          connectTimeout:Int =5000,
          readTimeout:Int =5000,
          requestMethod: String = "GET") = {
      import java.net.{URL, HttpURLConnection}
      val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      val inputStream = connection.getInputStream
      val content = io.Source.fromInputStream(inputStream).mkString
      if (inputStream != null) inputStream.close
      content
  }


  /**
   * @brief Download the data and write them into MongoDB
   *
   * This function is called repeatedly
   *
   * @return
   */
  def process(url: String): Unit = {
    // Get the data from the server
    try {
      val data = get(url)

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
    } catch {
      case ioe: java.io.IOException =>
        println("IO Exception in http resquest")
      case ste: java.net.SocketTimeoutException =>
        println("Socket timeout exception in http resquest")
    }
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


/**
 * @brief Client job, executed at regular interval
 */
class ClientJob extends Job {

  private final val urlStart = "http://129.194.185.199:5000/sensors/"
  private final val urlEnd = "/all_measures"

  def execute(context: JobExecutionContext) {
    println("Insert at : " + new Date)
    for (sensorNumber <- 2 to 16) {
      println("Getting info from sensor : " + sensorNumber)
      val url = urlStart + sensorNumber + urlEnd
      Client.process(url)
    }
  }
}


/**
 * @brief Main class
 *
 * Start the quartz job, calling the process function,
 * which get data from Raspberry PI and store it in the database
 * every 5 minutes
 */
object Main extends App {
  val scheduler = StdSchedulerFactory.getDefaultScheduler()

  scheduler.start()

  // Define the job and tie it to our ClientJob class
  val job = newJob(classOf[ClientJob]).withIdentity("jobClient", "groupClient").build()

  // Trigger the job to run now, and then repeat every 5 minutes
  val trigger = newTrigger()
    .withIdentity("triggerClient", "groupClient")
    .startNow()
    .withSchedule(simpleSchedule()
      .withIntervalInMinutes(4)
      .repeatForever())
    .build();

  // Tell quartz to schedule the job using our trigger
  scheduler.scheduleJob(job, trigger)
}
