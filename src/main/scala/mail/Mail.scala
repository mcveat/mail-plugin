package mail

import akka.util.Timeout
import play.twirl.api.Html
import io.Source
import javax.mail.Message.RecipientType
import org.codemonkey.simplejavamail.Email
import javax.mail.internet.MimeUtility
import play.api.Play.current
import akka.pattern.ask
import scala.concurrent.Future
import scala.util.{Try, Failure}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/** Provides [[mail.Mail]] instance factory method, case classes and implicits needed to make it work */
object Mail {
  /** Creates empty Mail instance */
  def apply() = new Mail[UNSET, UNSET, UNSET, UNSET]()
  /** Allows mail instance with all required fields set to be sent */
  implicit def enableSending(mail: Mail[SET, SET, SET, SET]): Object { def send(): Future[Unit] } = new {
    implicit val timeout = Timeout(1.minute)
    def send(): Future[Unit] = {
      (MailActor.get ? toEmail).mapTo[Try[Unit]].flatMap {
        case Failure(e) => Future.failed(e)
        case _ => Future.successful(())
      }
    }
    private def toEmail = {
      val e = new Email()
      mail.from.foreach((e.setFromAddress _).tupled)
      e.setSubject(mail.subject.get)
      mail.recipients.foreach {
        case ReplyTo(name, address) => e.setReplyToAddress(name, address)
        case r: TypedRecipient => e.addRecipient(r.name, r.address, r.rType)
      }
      mail.text.foreach(e.setText)
      mail.html.foreach(h => e.setTextHTML(h.toString))
      mail.attachments.foreach { a =>
        e.addAttachment(MimeUtility.encodeText(a.name), a.data.map(_.toByte).toArray, a.mimeType)
      }
      e
    }
  }

  case class Attachment(name: String, data: Source, mimeType: String)

  abstract class Recipient {
    val name: String
    val address: String
  }
  abstract class TypedRecipient(val rType: RecipientType) extends Recipient
  case class To(name: String, address: String) extends TypedRecipient(RecipientType.TO)
  case class Bcc(name: String, address: String) extends TypedRecipient(RecipientType.BCC)
  case class Cc(name: String, address: String) extends TypedRecipient(RecipientType.CC)
  case class ReplyTo(name: String, address: String) extends Recipient

  /** marker class for statically typed builder pattern */
  abstract class UNSET
  /** marker class for statically typed builder pattern */
  abstract class SET
}

/** Mail instance builder
  *
  * Utilizes statically typed builder pattern. Only instance with `from`, `to`, `subject`and `body`(either text or html)
  * values set can be actually sent to [[mail.MailActor.MailActor]].
  */
class Mail[FROM, TO, SUBJECT, BODY](val from: Option[(String, String)] = None, val subject: Option[String] = None,
                                    val recipients: List[Mail.Recipient] = List.empty, val text: Option[String] = None,
                                    val html: Option[Html] = None, val attachments: List[Mail.Attachment] = List.empty) {
  import Mail._
  /** Returns new instance with from field set to given tuple (with `(name, address)` fields) */
  def from(f: (String, String)) = new Mail[SET, TO, SUBJECT, BODY](Some(f), subject, recipients, text, html, attachments)
  /** Returns new instance with from field set to given values */
  def from(name: String, address: String) =
    new Mail[SET, TO, SUBJECT, BODY](Some((name, address)), subject, recipients, text, html, attachments)
  /** Returns new instance with recipients list replaced with given list. */
  def withRecipients(r: List[Recipient]) =
    new Mail[FROM, SET, SUBJECT, BODY](from, subject, r, text, html, attachments)
  /** Returns new instance with recipients list replaced with given recipients. */
  def withRecipients(to_x: Recipient, to_xs: Recipient*) =
    new Mail[FROM, SET, SUBJECT, BODY](from, subject, to_x :: to_xs.toList, text, html, attachments)
  /** Returns new instance with `TO:` recipient appended */
  def to(name: String, address: String) = withRecipients(To(name, address) :: recipients)
  /** Returns new instance with `CC:` recipient appended */
  def cc(name: String, address: String) = withRecipients(Cc(name, address) :: recipients)
  /** Returns new instance with `BCC:` recipient appended */
  def bcc(name: String, address: String) = withRecipients(Bcc(name, address) :: recipients)
  /** Returns new instance with reply-to set to given value */
  def replyTo(name: String, address: String) = withRecipients(ReplyTo(name, address) :: recipients)
  /** Returns new instance with subject set to given value */
  def withSubject(s: String) = new Mail[FROM, TO, SET, BODY](from, Some(s), recipients, text, html, attachments)
  /** Returns new instance with text body set to given value */
  def withText(t: String) = new Mail[FROM, TO, SUBJECT, SET](from, subject, recipients, Some(t), html, attachments)
  /** Returns new instance with html body set to given value */
  def withHtml(h: Html) = new Mail[FROM, TO, SUBJECT, SET](from, subject, recipients, text, Some(h), attachments)
  /** Returns new instance with attachments list replaced with given attachments. */
  def withAttachments(a: List[Attachment]) = new Mail[FROM, TO, SUBJECT, BODY](from, subject, recipients, text, html, a)
  /** Returns new instance with attachments list replaced with given attachments. */
  def withAttachments(a_x: Attachment, a_xs: Attachment*) =
    new Mail[FROM, TO, SUBJECT, BODY](from, subject, recipients, text, html, a_x :: a_xs.toList)
  /** Returns new instance with attachment appended to attachments list */
  def withAttachment(name: String, data: Source, mimeType: String) =
    new Mail[FROM, TO, SUBJECT, BODY](from, subject, recipients, text, html, Attachment(name, data, mimeType) :: attachments)
}
