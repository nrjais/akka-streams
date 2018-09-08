
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.6",
      version := "0.1.0-SNAPSHOT",
      transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)
    )),
    name := "scala-bootcamp",
    libraryDependencies += Libs.`akka-http`,
    libraryDependencies += Libs.`scalatest`
  )
