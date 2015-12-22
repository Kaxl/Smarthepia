

import scala.io.Source._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._


object Client {

  private final val urlAll = "http://129.194.185.199:5000/sensors/3/all_measures"
  // Database connection
  val driver = new MongoDriver
  val connection = driver.connection(List("86.119.33.122:27017"))

  //val db = connection("SmarthepiaDB")
  val db = connection("testAF")
  val collection = db.collection("sensors")

  def get(url: String) = scala.io.Source.fromURL(url).mkString


  def main(args: Array[String]): Unit = {
    val data = get(urlAll)
    println(data)
    //val j = data.parseJson
    //println(j)

    val document = BSONDocument(
      "firstName" -> "Stephane",
      "lastName" -> "Godbillon",
      "age" -> 29)

    val future = collection.insert(document)

    //future.onComplete {
    //  case Failure(e) => throw e
    //  case Success(lastError) => {
    //    println("successfully inserted document with lastError = " + lastError)
    //  }
    //}
    //collection.insert(j).map(lastError =>
    //    println("Mongo LastError: %s".format(lastError)))
    sys.exit(0)
  }
}
