import io.Source
import play.api.libs.MimeTypes
import play.api.test._
import Helpers._
import mail._
import Mail._
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success

class MailSpec extends Specification {
  "Mail" >> {
    "send dummy email using mock" >> {
      running(FakeApplication(additionalConfiguration = Map("mail.mock" -> "true"))) {
        val attachment = Source.fromBytes("Ninja should wear black".toCharArray.map(_.toByte))
        val result = Mail()
          .from("sender", "sender@example.com")
          .to("receiver", "receiver@example.com")
          .replyTo("ninja master", "master@ninja.com")
          .withSubject("A subject")
          .withText("body")
          .withAttachments(Attachment("ninja code", attachment, MimeTypes.forExtension("txt").get))
          .send()
        Await.ready(result, 5.seconds).value match {
          case Some(Success(())) => success
          case _ => failure
        }
      }
    }
  }
}
