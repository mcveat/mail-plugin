package mail

import javax.inject.Inject

import org.codemonkey.simplejavamail.{Mailer, TransportStrategy}
import play.api.{Configuration, Application, Logger}

trait MailComponent

class MailComponentProvider @Inject() (implicit app: Application) extends MailComponent {
  val logger = Logger("mail")

  private val DEFAULT_HOST = "localhost"
  private val DEFAULT_PORT = 25

  private lazy val smtpConf = app.configuration.getConfig("smtp").getOrElse(Configuration.empty)
  private lazy val mock = app.configuration.getBoolean("mail.mock").exists(identity)
  private lazy val host = smtpConf.getString("host").getOrElse(DEFAULT_HOST)
  private lazy val port = smtpConf.getInt("port").getOrElse(DEFAULT_PORT)
  private lazy val username = smtpConf.getString("username").getOrElse("")
  private lazy val password = smtpConf.getString("password").getOrElse("")
  private lazy val transport =
    smtpConf.getString("transport").map(TransportStrategy.valueOf).getOrElse(TransportStrategy.SMTP_PLAIN)

  private lazy val mailer = new Mailer(host, port, username, password, transport)

  if (mock) {
    MailActor.startWithMock
    logger.info("Started using mocked mailer")
  } else {
    MailActor.startWith(mailer)
    logger.info("Started with smtp server on %s:%s".format(host, port))
  }
}
