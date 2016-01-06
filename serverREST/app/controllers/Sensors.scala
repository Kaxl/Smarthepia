package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._

// Java date
import java.text.SimpleDateFormat

/**
 * The Sensors controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
 * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
@Singleton
class Sensors extends Controller with MongoController {

  // Default date from request
  private final val DATE_START_DEFAULT = "20000101"
  private final val DATE_END_DEFAULT = "20500101"

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Sensors])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("sensors")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models._
  import models.JsonFormatsSensor._

  /**
   * @brief Get data of typeSensor of a sensor ID from a Raspberry between two dates
   *
   * @param typeSensor  Type of sensor
   * @param sensorID    ID of the sensor
   * @param piID        ID of the Raspberry
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return
   */
  def getValueSensor(typeSensor: String, sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("sensor" -> sensorID, "controller" -> piID, "updateTime" -> Json.obj("$gt" -> dteStartUnix, "$lt" -> dteEndUnix)), Json.obj(typeSensor -> 1, "updateTime" -> 1, "_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> -1)).
      // Perform the query and get a cursor of JsObject
      cursor[JsObject]
      // Put all the JsObjects in a list
      val futureSensorList: Future[List[JsObject]] = cursor.collect[List]()

      // Transform the list into a JsArray
      val futureSensorsJsonArray: Future[JsArray] = futureSensorList.map { sensors =>
        Json.arr(sensors)
      }
      // Reply with the array
      futureSensorsJsonArray.map {
        sensors =>
          Ok(sensors(0))
      }
  }

  def getTemperature(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getValueSensor("temperature", sensorID, piID, dteStart, dteEnd)
  }

  def getHumidity(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getValueSensor("humidity", sensorID, piID, dteStart, dteEnd)
  }

  def getLuminance(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getValueSensor("luminance", sensorID, piID, dteStart, dteEnd)
  }

  /**
   * @brief Get all values of a sensors ID of a Raspberry
   *
   * @param sensorID    ID of the sensor
   * @param piID        ID of the Raspberry
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return 
   */
  def getAllFromID(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("sensor" -> sensorID, "controller" -> piID, "updateTime" -> Json.obj("$gt" -> dteStartUnix, "$lt" -> dteEndUnix)), Json.obj("_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> -1)).
      // Perform the query and get a cursor of JsObject
      cursor[JsObject]
      // Put all the JsObjects in a list
      val futureSensorList: Future[List[JsObject]] = cursor.collect[List]()

      // Transform the list into a JsArray
      val futureSensorsJsonArray: Future[JsArray] = futureSensorList.map { sensors =>
        Json.arr(sensors)
      }
      // Reply with the array
      futureSensorsJsonArray.map {
        sensors =>
          Ok(sensors(0))
      }
  }

  /**
   * @brief Get all values of a Raspberry
   *
   * @param piID        ID of the Raspberry
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return 
   */
  def getAllFromPI(piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("controller" -> piID, "updateTime" -> Json.obj("$gt" -> dteStartUnix, "$lt" -> dteEndUnix)), Json.obj("_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> -1)).
      // Perform the query and get a cursor of JsObject
      cursor[JsObject]
      // Put all the JsObjects in a list
      val futureSensorList: Future[List[JsObject]] = cursor.collect[List]()

      // Transform the list into a JsArray
      val futureSensorsJsonArray: Future[JsArray] = futureSensorList.map { sensors =>
        Json.arr(sensors)
      }
      // Reply with the array
      futureSensorsJsonArray.map {
        sensors =>
          Ok(sensors(0))
      }
  }

  /**
   * @brief Get the Unix time from a string date in format "yyyyMMdd"
   *
   * @param dte   Date to parse
   *
   * @return The Unix timestamp
   */
  def getUnixTimeFromString(dte: String): Long = {
    val stringFormat = new SimpleDateFormat("yyyyMMdd")
    stringFormat.parse(dte).getTime() / 1000
  }
}

