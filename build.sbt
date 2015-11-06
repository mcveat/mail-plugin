scalaVersion := "2.11.7"

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
    "-language:reflectiveCalls",
    "-Yrangepos"
)

libraryDependencies ++= Seq(
  "org.codemonkey.simplejavamail" % "simple-java-mail" % "2.4",
  "com.typesafe.play" %% "play" % "2.4.3" % "provided",
  "com.typesafe.play" %% "twirl-api" % "1.1.1" % "provided",
  "com.typesafe.play" %% "play-test" % "2.4.3" % "test",
  "org.specs2" %% "specs2-core" % "3.6.5" % "test",
  "junit" % "junit" % "4.12" % "test"
)
