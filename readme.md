A Play 2.x plugin providing a scala wrapper to simple-java-mail
===============================================================

Build status: [![Build Status](https://secure.travis-ci.org/mcveat/mail-plugin.png?branch=master)](https://travis-ci.org/mcveat/mail-plugin)

[Scaladoc](http://mcveat.github.io/mail-plugin/api/0.6/index.html)

Installation
============

As ivy artifact
---------------

To your `Build.scala` add:

    resolvers += (
        Resolver.url("mcveat.github.com", url("http://mcveat.github.com/releases"))(Resolver.ivyStylePatterns)
    )

and `"play.modules.mail" %% "play2-mail-plugin" % "0.6"` as a dependency.

As a binary
-----------

Checkout the project, build it from the sources with `sbt package` command. Then either:
* put the jar available in `target/scala-2.x` to the lib folder of your play app
* publish it localy with `sbt publish-local` and add `"play.modules.mail" %% "play2-mail-plugin" % "0.7-SNAPSHOT"` to your build settings.

As a Git submodule
------------------
You can add it as a submodule of your play project.
Checkout the project in modules/mail-plugin, then do `git submodule add`

In your project Build.scala add the dependency to the plugin :

        val mailPlugin = Project("mailPlugin", file("modules/mail-plugin"))
        val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA)
                    .dependsOn(mailPlugin)


Usage
=====

        import mail._
        import Mail._

        def sendMail = Action { request =>
            val attachment = Source.fromBytes("Ninja should wear black".toCharArray.map(_.toByte))
            val result = Mail()
                .from("sender", "sender@example.com")
                .to("receiver", "receiver@example.com")
                .replyTo("ninja master", "master@ninja.com")
                .withSubject("A subject")
                .withText("body")
                .withAttachments(Attachment("ninja code", attachment, "text/plain")
                .send()
            result.flatMap { _ => Ok("It works") }
        }

Mail class utilizes statically typed builder pattern, so `send()` method is not available before sender, receiver,
subject and message body (either text or html) is set.

Configuration
=============
In `application.conf` :

        #put this setting in you want to mock the mail server in development
        mail.mock=true

        #smtp server settings
        smtp.host=smtp.server.com
        smtp.port=25
        smtp.username=
        smtp.password=
        smtp.transport=

Supported transports: `SMTP_PLAIN` (default), `SMTP_SSL`, `SMTP_TLS`

Changelog
=========

version 0.6
-----------

* updated to Play Framework 2.4.3
* changed signature of a `send()` method from `Unit` to `Future[Unit]`

[scaladoc](http://mcveat.github.io/mail-plugin/api/0.6/index.html)

version 0.5
-----------

* updated to Play Framework 2.3.5

No API change.

version 0.4
-----------

* support for SSL/TSL transports in configuration
* updated to Play Framework 2.2.2

No API change. Thanks to [@tjjalava](https://github.com/tjjalava) for submitting the pull request.

version 0.3
-----------

* updated to Play Framework 2.1.1

No API change

version 0.2.1
-------------

* Bug fix: attachment name is encoded from now on

No API change

version 0.2
-----------

* Support for Reply-To header

[scaladoc](http://mcveat.github.io/mail-plugin/api/0.2/index.html)

version 0.1
-----------

* Initial release for Play Framework 2.0.3
* SMTP_PLAIN authententication
* FROM, TO, CC, BCC recipients fields supported
* Multipart content with mixed text and html
* Multiple attachments

[scaladoc](http://mcveat.github.io/mail-plugin/api/0.1/index.html)

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/mcveat/mail-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
