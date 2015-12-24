package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.json._

import scala.util._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.MongoDriver
import reactivemongo.bson.BSONDocument

import play.modules.reactivemongo.json._,ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

class Application extends Controller {

    val driver = new MongoDriver
    val connection = driver.connection(List("86.119.33.122"))

    val db = connection.db("SmarthepiaDB")
    //val collection = db.collection("sensors")
    def collection: JSONCollection = db.collection("TestAF")

    val document = Json.obj(
        "battery" -> 100,
        "controller" -> "Pi 3",
        "humidity" -> 23,
        "location" -> "A401",
        "luminance" -> 12,
        "motion" -> true,
        "sensor" -> 3,
        "temperature" -> 25.8,
        "updateTime" -> 1450363523
    )

    //def index = Action {
    //    try {
    //        val future = collection.insert(document)

    //        future.onComplete {
    //          case Failure(e) => throw e
    //          case Success(lastError) => {
    //            Ok("successfully inserted document with lastError = " + lastError)
    //          }
    //        }
    //    } finally {
    //        connection.close()
    //    }
    //    Ok("Insert done")
    //}

    def index = Action.async {
        WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
            val json = Json.parse(response)
            collection.insert(json)
        }
    }

<<<<<<< HEAD
    //def index = Action.async {
    //    WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
    //        Ok(response.json)
    //    }
    //}

    //def index = Action.async {
    //    WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
    //        collection.insert(response.json).map(lastError =>
    //            OK("Mongo LastErro: %s".format(lastError)))
    //    }
    //}

    //def getSensor = Action.async {
    //    //Ok(views.html.index("Your new application is ready."))
    //    WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
    //        Ok(response.json)
    //    }
    //}

    //def writeData = {
    //    val msg = getSensor()
    //    collection.insert(response.json).map(lastError =>
    //            OK("Mongo LastErro: %s".format(lastError)))
    //}
=======
    def getSensor = Action.async {
        //Ok(views.html.index("Your new application is ready."))
        WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
            OK(response.json)
        }
    }

    def writeData = {
        val msg = getSensor()
        collection.insert(response.json).map(lastError =>
                OK("Mongo LastErro: %s".format(lastError)))
    }
>>>>>>> cf952d14ae4ec5bc3319d109a8e1d0c3974b8f31
}


class w {




}

