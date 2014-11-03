scalaVersion := "2.10.4"

name := "play2-mail-plugin"

organization := "play.modules.mail"

version := "0.6-SNAPSHOT"

resolvers ++= Seq(
    "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:reflectiveCalls"
)

libraryDependencies ++= Seq(
  "org.codemonkey.simplejavamail" % "simple-java-mail" % "2.1",
  "com.typesafe.play" %% "play" % "2.3.5" % "provided",
  "com.typesafe.play" %% "twirl-api" % "1.0.2" % "provided",
  "com.typesafe.play" %% "play-test" % "2.3.5" % "test",
  "org.specs2" %% "specs2" % "2.4.5-scalaz-7.0.6" % "test",
  "junit" % "junit" % "4.8" % "test"
)
