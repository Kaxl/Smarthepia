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

object JsonFormatsSensor {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Sensor thanks to Json Macros
  implicit val sensorFormat = Json.format[Sensor]
}
