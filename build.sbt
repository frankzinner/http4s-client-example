ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / scalacOptions := Seq(
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-feature",
  //  "-explain"
)

Compile / run / fork := true

val catsCore = "2.11.0"
val catsEffectsVersion = "3.5.4"
val scalaTestVersion = "3.2.15"
val fs2Version = "3.10.2"
val http4sVersion = "0.23.26"
val scalatestplus = "3.2.15.0"
val scalacheck = "1.17.0"
val circeversion = "0.14.6"
val log4catsVersion = "2.7.0"

addCommandAlias("cc", "clean;compile")

lazy val root = (project in file("."))
  .settings(
    name := "client",
    libraryDependencies ++= Seq(
      // Testing libs
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-17" % scalatestplus % Test,
      "org.scalacheck" %% "scalacheck" % scalacheck % Test,
      // typelevel cats libs
      "org.typelevel" %% "cats-core" % catsCore,
      "org.typelevel" %% "cats-effect" % catsEffectsVersion,
      // typelevel http4s libs
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      // circe json libs
      "org.http4s" %% "http4s-circe" % http4sVersion,
      // Optional for auto-derivation of JSON codecs
      "io.circe" %% "circe-generic" % circeversion,
      // Optional for string interpolation to JSON model
      "io.circe" %% "circe-literal" % circeversion,
      // typelevel logging
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "ch.qos.logback" % "logback-classic" % "1.3.5"
    ),
  )
