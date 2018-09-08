import sbt._

object Libs {
  val akkaVersion = "10.1.5"
  val Org         = "com.typesafe.akka"

  val `akka-http`   = Org             %% "akka-http"  % akkaVersion
  val `scalatest`   = "org.scalatest" %% "scalatest"  % "3.0.5" % Test
  val `akka-stream` = Org             % "akka-stream" % "2.5.16"
}
