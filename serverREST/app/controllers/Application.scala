package controllers

import javax.inject.{Singleton, Inject}
import play.api.libs.json._
import play.api.mvc._
import org.slf4j.{LoggerFactory, Logger}
import play.api.http.HeaderNames
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
 * Main application
 *
 * Needs to be a class because it is the default route (/)
 */
class Application @Inject() extends Controller {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  /*
   * @brief Default action
   */
  def index = Action {
    logger.info("Starting REST server...");

    // Message to print on the / page
    Ok("Smarthepia REST server is running...");
  }

  def options(path: String) = CorsAction {
    Action { request =>
      Ok.withHeaders(ACCESS_CONTROL_ALLOW_HEADERS -> Seq(AUTHORIZATION, CONTENT_TYPE, "Target-URL").mkString(","))
    }
  }
}

// Adds the CORS header
case class CorsAction[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[Result] = {
    action(request).map(result => result.withHeaders(HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
    HeaderNames.ALLOW -> "*",
    HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, PUT, DELETE, OPTIONS",
    HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent"
    ))
  }

  lazy val parser = action.parser
}
