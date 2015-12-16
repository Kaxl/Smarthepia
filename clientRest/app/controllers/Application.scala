package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS

import reactivemongo.api.MongoDriver
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

class Application extends Controller {

    val driver = new MongoDriver
    val connection = driver.connection(List("86.119.33.122"))

    val db = connection.db("SmarthepiaDB")
    val collection = db.collection("sensors")


    def index = Action.async {
        WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
            collection.insert(response.json).map(lastError =>
                OK("Mongo LastErro: %s".format(lastError)))
        }
    }

    def getSensor = Action.async {
        //Ok(views.html.index("Your new application is ready."))
        WS.url("http://129.194.185.199:5000/sensors/3/all_measures").get().map { response =>
            Ok(response.json)
        }
    }

    def writeData = {
        val msg = getSensor()
        collection.insert(response.json).map(lastError =>
                OK("Mongo LastErro: %s".format(lastError)))
    }
}


class w {




}

