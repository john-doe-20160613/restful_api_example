import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "restful_api_example",
    scalaVersion := "2.12.6",
    version := "0.1",
    libraryDependencies ++= Seq(
      Akka.http,
      Akka.stream,
      Akka.sprayJson
    ) ++ Dependencies.test(
      "org.specs2" %% "specs2-core" % "4.2.0",
      "org.scalatest" %% "scalatest" % "3.0.5",
      Akka.actorTestkit,
      Akka.httpTestkit
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
