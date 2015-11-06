package mail

import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}

class MailModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
   bind[MailComponent].to[MailComponentProvider].eagerly()
  )
}
