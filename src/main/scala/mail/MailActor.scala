package mail

import play.api.Logger
import play.api.libs.concurrent._
import akka.actor.{Actor, Props}
import org.codemonkey.simplejavamail.{MailException, Email, Mailer}
import play.api.Play.current

/** Factory for mailer actors ([[mail.MailActor.MailActor]] and [[mail.MailActor.MailActorMock]]). Provides access
  * to actor reference.
  */
object MailActor {
  private val actorName: String = "mailer"
  /** Creates mailer actor backed by mailer specified in parameter ([[mail.MailActor.MailActor]]) */
  def startWith(mailer: Mailer) = Akka.system.actorOf(Props(new MailActor(mailer)), name = actorName)
  /** Creates mocked mailer actor ([[mail.MailActor.MailActorMock]]) */
  def startWithMock = Akka.system.actorOf(Props(new MailActorMock()), name = actorName)
  /** Looks up for mailer actor instance */
  def get = Akka.system.actorSelection("/user/%s".format(actorName))

  /** Sends email when receiving instance of Email.
    *
    * Uses Mailer instance given as constructor parameter.
    */
  class MailActor(mailer: Mailer) extends Actor {
    def receive = {
      case email: Email => {
        try {
          mailer.sendMail(email)
          Logger.debug("MailPlugin: email sent")
        } catch {
          case e:MailException => {
            Logger.error("MailPlugin:"+e.getMessage)
          }
        }
      }
    }
  }

  /** Logs event of receiving Email instance as message. */
  class MailActorMock extends Actor {
    def receive = {
      case email: Email => {
        Logger.debug("MailPlugin: email sent to mock")
      }
    }
  }
}
