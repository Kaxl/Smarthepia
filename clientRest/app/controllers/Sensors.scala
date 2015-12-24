package controllers

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import reactivemongo.api._
import play.modules.reactivemongo.ReactiveMongoApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._

//class Sensors extends Controller with MongoController with ReactiveMongoComponents {
//
//    private final val logger: Logger = LoggerFactory.getLogger(classOf[Sensors])
//
//    def collection: JSONCollection = db.collection[JSONCollection]("sensors")
//
//    import models._
//    import models.JsonFormats._
//
//    def createEntry = Action.async(parse.json) {
//        request =>
//            request.body.validate[Sensor].map {
//                sensor =>
//                    // 'sensor' is an instance of the case class 'models.Sensor'
//                    collection.insert(sensor).map {
//                        lastError =>
//                            logger.debug(s"Successfully inserted with lastError: $lastError")
//                            Created(s"Sensor entry added")
//                    }
//            }.getOrElse(Future.successful(BadRequest("invalid json")))
//    }
//}
