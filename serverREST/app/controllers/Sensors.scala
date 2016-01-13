package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import reactivemongo.bson._
import reactivemongo.core.commands._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._

// Scala date
import com.github.nscala_time.time.Imports._
import org.joda.time.Days

// Java date
import java.text.SimpleDateFormat


// test with json
case class Data(updateTime: Long, value: Double)

object Data {
  implicit val dataReader = Json.reads[Data]
}

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
   * Get data of typeSensor of a sensor ID from a Raspberry between two dates
   *
   * @param typeSensor  Type of sensor
   * @param sensorID    ID of the sensor
   * @param piID        ID of the Raspberry
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return
   */
  def getSensorValue(typeSensor: String, sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("sensor" -> sensorID, "controller" -> piID, "updateTime" -> Json.obj("$gte" -> dteStartUnix, "$lte" -> dteEndUnix)), Json.obj(typeSensor -> 1, "updateTime" -> 1, "_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> 1)).
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
   * Get data of typeSensor from a Room
   *
   * @param typeSensor  Type of sensor
   * @param roomName    Name of the room
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return
   */
  def getRoomValue(typeSensor: String, roomName: String, dteStart: Option[String], dteEnd: Option[String]) = {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("location" -> roomName, "updateTime" -> Json.obj("$gte" -> dteStartUnix, "$lte" -> dteEndUnix)), Json.obj("sensor" -> 1, typeSensor -> 1, "updateTime" -> 1, "_id" -> 0)).
      // Sort them by sensor and by updateTime
      sort(Json.obj("sensor" -> 1, "updateTime" -> 1)).
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
   * Sensors
   * Method to get a special field of the sensors by sensor ID
   */
  def getTemperatureSensor(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getSensorValue("temperature", sensorID, piID, dteStart, dteEnd)
  }

  def getHumiditySensor(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getSensorValue("humidity", sensorID, piID, dteStart, dteEnd)
  }

  def getLuminanceSensor(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getSensorValue("luminance", sensorID, piID, dteStart, dteEnd)
  }

  def getMotionSensor(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getSensorValue("motion", sensorID, piID, dteStart, dteEnd)
  }

  /**
   * Rooms
   * Method to get a special field of the sensors by location (room)
   */
  def getTemperatureRoom(name: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getRoomValue("temperature", name, dteStart, dteEnd)
  }

  def getHumidityRoom(name: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getRoomValue("humidity", name, dteStart, dteEnd)
  }

  def getLuminanceRoom(name: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getRoomValue("luminance", name, dteStart, dteEnd)
  }

  def getMotionRoom(name: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    getRoomValue("motion", name, dteStart, dteEnd)
  }

  /**
   * Get all values of a sensors ID of a Raspberry
   *
   * @param sensorID    ID of the sensor
   * @param piID        ID of the Raspberry
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return
   */
  def getAllFromSensor(sensorID: Int, piID: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("sensor" -> sensorID, "controller" -> piID, "updateTime" -> Json.obj("$gte" -> dteStartUnix, "$lte" -> dteEndUnix)), Json.obj("_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> 1)).
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
   * Get all values of room
   *
   * @param name        Name of the room
   * @param dteStart    Date start
   * @param dteEnd      Date end
   *
   * @return
   */
  def getAllFromRoom(name: String, dteStart: Option[String], dteEnd: Option[String]) = Action.async {
    // Parse the date, check if provided, else provide default
    val dStart : String = dteStart.getOrElse(DATE_START_DEFAULT)
    val dEnd : String = dteEnd.getOrElse(DATE_END_DEFAULT)
    // Conversion of date to Unix format
    val dteStartUnix = getUnixTimeFromString(dStart)
    val dteEndUnix = getUnixTimeFromString(dEnd)
    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("location" -> name, "updateTime" -> Json.obj("$gte" -> dteStartUnix, "$lte" -> dteEndUnix)), Json.obj("_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> 1)).
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
   * Get all values of a Raspberry
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
      find(Json.obj("controller" -> piID, "updateTime" -> Json.obj("$gte" -> dteStartUnix, "$lte" -> dteEndUnix)), Json.obj("_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> 1)).
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
   * Get list of Sensors which have a battery below (or equal) the given pourcentage
   *
   * @param piID          ID of the Raspberry
   * @param pourcentage   Pourcentage limit
   *
   * @return
   */
  def getBattery(piID: String, pourcentage: Int) = Action.async {
    // Calculate the date/time of 4 minutes ago in order to get
    // last values of battery for each sensors
    val sinceDate = DateTime.now - 4.minutes
    val sinceUpdateTime = sinceDate.getMillis() / 1000
    println(sinceDate)
    println(sinceUpdateTime)

    val cursor: Cursor[JsObject] = collection.
      // Find between the two dates
      find(Json.obj("controller" -> piID, "battery" -> Json.obj("$lte" -> pourcentage), "updateTime" -> Json.obj("$gte" -> sinceUpdateTime)), Json.obj("sensor" -> 1, "battery" -> 1, "updateTime" -> 1, "_id" -> 0)).
      // Sort them by updateTime
      sort(Json.obj("updateTime" -> -1)).
      // Limit to 1 record (last value)
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
   * Get the Unix time from a string date in format "yyyyMMdd"
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

