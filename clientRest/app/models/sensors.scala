package models

case class Sensor( battery: Int,
                   controller: String,
                   humidity: Int,
                   location: String,
                   luminance: Int,
                   motion: Boolean,
                   sensor: Int,
                   temperatur: Double,
                   updateTime: String)

object JsonFormats {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed and User thanks to Json Macros
    implicit val sensorFormat = Json.format[Sensor]
}

