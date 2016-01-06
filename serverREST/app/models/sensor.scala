package models

import play.api.libs.json.Json

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

//object Sensor{
//
//
//}
//object MyClass {
//  implicit val readsMyClass: Reads[MyClass] = new Reads[MyClass] {
//    def reads(json: JsValue): JsResult[MyClass] = {
//      for {
//        suggestion <- (json \ "suggestion").validate[String]
//        categories <- (json \ "categories").validate[String]
//      } yield MyClass(json,categories)
//    }
//  }
//}
///**
// * @brief Sensor object to write into BSON
// */
//object Sensor {
//  implicit object SensorWriter extends BSONDocumentWriter[Sensor] {
//    def write(sensor: Sensor): BSONDocument = BSONDocument(
//      "battery" -> sensor.battery,
//      "controller" -> sensor.controller,
//      "humidity" -> sensor.humidity,
//      "location" -> sensor.location,
//      "luminance" -> sensor.luminance,
//      "motion" -> sensor.motion,
//      "sensor" -> sensor.sensor,
//      "temperature" -> sensor.temperature,
//      "updateTime" -> sensor.updateTime
//    )
//  }
//
//  implicit object SensorReader extends BSONDocumentReader[Sensor] {
//    def read(doc: BSONDocument): Sensor = Sensor(
//      doc.getAs[Int]("battery").get,
//      doc.getAs[String]("controller").get,
//      doc.getAs[Int]("humidity").get,
//      doc.getAs[String]("location").get,
//      doc.getAs[Int]("luminance").get,
//      doc.getAs[Boolean]("motion").get,
//      doc.getAs[Int]("sensor").get,
//      doc.getAs[Double]("temperature").get,
//      doc.getAs[Long]("updateTime").get
//    )
//  }
//}

object JsonFormatsSensor {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Sensor thanks to Json Macros
  implicit val sensorFormat = Json.format[Sensor]
}
