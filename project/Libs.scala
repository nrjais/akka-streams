import sbt._

object Libs {
  val akkaVersion = "10.1.5"
  val Org         = "com.typesafe.akka"

  val `play-json`           = "com.typesafe.play"      %% "play-json"           % "2.6.10"
  val `akka-http-play-json` = "de.heikoseeberger"      %% "akka-http-play-json" % "1.21.0"
  val `akka-http`           = Org                      %% "akka-http"           % akkaVersion
  val `scalatest`           = "org.scalatest"          %% "scalatest"           % "3.0.5"
  val `akka-stream`         = Org                      %% "akka-stream"         % "2.5.16"
  val `scala-async`         = "org.scala-lang.modules" %% "scala-async"         % "0.9.7"
}
