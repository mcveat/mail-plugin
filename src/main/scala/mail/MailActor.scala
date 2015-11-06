package mail

import play.api.{Application, Logger}
import play.api.libs.concurrent._
import akka.actor.{Actor, Props}
import org.codemonkey.simplejavamail.{MailException, Email, Mailer}

/** Factory for mailer actors ([[mail.MailActor.MailActor]] and [[mail.MailActor.MailActorMock]]). Provides access
  * to actor reference.
  */
object MailActor {
  private val actorName: String = "mailer"
  private val logger = Logger("mail")

  /** Creates mailer actor backed by mailer specified in parameter ([[mail.MailActor.MailActor]]) */
  def startWith(mailer: Mailer)(implicit app: Application) = startUsing(new MailActor(mailer))
  /** Creates mocked mailer actor ([[mail.MailActor.MailActorMock]]) */
  def startWithMock(implicit app: Application) = startUsing(new MailActorMock())

  private def startUsing(actor: => Actor)(implicit app: Application): Unit =
      Akka.system.actorOf(Props(actor), name = actorName)

  /** Looks up for mailer actor instance */
  def get(implicit app: Application) = Akka.system.actorSelection("/user/%s".format(actorName))

  /** Sends email when receiving instance of Email.
    *
    * Uses Mailer instance given as constructor parameter.
    */
  class MailActor(mailer: Mailer) extends Actor {
    def receive = {
      case email: Email =>
        try {
          mailer.sendMail(email)
          logger.debug("Email sent")
        } catch { case e:MailException =>
          logger.error("Sending email failed", e)
        }
    }
  }

  /** Logs event of receiving Email instance as message. */
  class MailActorMock extends Actor {
    def receive = {
      case _: Email => logger.debug("Email sent to mock")
    }
  }
}
