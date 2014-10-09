package mail

import play.api._
import org.codemonkey.simplejavamail.{TransportStrategy, Mailer}

/** Play plugin implementation for mailer
  *
  * Put '`400:mail.MailPlugin`' into `conf/play.plugins` to make it work.
  *
  * Recognized configuration properties (to be set in `application.conf`):
  *  - '''mail.mock''':     creates mocked mailer actor on startup and omits any other settings if set to `true`
  *  - '''smtp.host''':     hostname of smtp server to be used, defaults to `localhost`
  *  - '''smtp.port''':     port of smtp server to be used, defaults to `25`
  *  - '''smtp.username''': username to be used when accessing smtp server, defaults to empty string
  *  - '''smtp.password''': password to be used when accessing smtp server, defaults to empty string
  */
class MailPlugin(app:Application) extends Plugin {
  private val DEFAULT_HOST = "localhost"
  private val DEFAULT_PORT = 25

  private lazy val mock = app.configuration.getBoolean("mail.mock") getOrElse false
  private lazy val host = app.configuration.getString("smtp.host") getOrElse DEFAULT_HOST
  private lazy val port = app.configuration.getInt("smtp.port") getOrElse DEFAULT_PORT
  private lazy val username = app.configuration.getString("smtp.username") getOrElse ""
  private lazy val password = app.configuration.getString("smtp.password") getOrElse ""
  private lazy val transport = app.configuration.getString("smtp.transport").map { trans =>
    TransportStrategy.valueOf(trans)
  } getOrElse TransportStrategy.SMTP_PLAIN

  private def mailer = new Mailer(host, port, username, password, transport)

  override def onStart() {
    Logger.debug("Mail plugin starting... ")
    if (mock) {
      MailActor.startWithMock
      Logger.info("Mail plugin successfully started using mocked mailer")
    }
    else {
      MailActor.startWith(mailer)
      Logger.info("Mail plugin successfully started with smtp server on %s:%s".format(host, port))
    }
  }
}
