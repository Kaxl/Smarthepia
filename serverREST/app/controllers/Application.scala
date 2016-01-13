package controllers

import javax.inject.{Singleton, Inject}
import play.api.libs.json._
import play.api.mvc._
import org.slf4j.{LoggerFactory, Logger}


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
}
