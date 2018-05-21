import sbt._

object Dependencies {

  def test(modules: ModuleID*): Seq[ModuleID] = modules.map(_ % Test)

  object Akka {

    lazy val http = groupId %% "akka-http" % akkaHttpVersion
    lazy val sprayJson = groupId %% "akka-http-spray-json" % akkaHttpVersion
    lazy val actor = groupId %% "akka-actor" % akkaVersion
    lazy val stream = groupId %% "akka-stream" % akkaVersion
    lazy val httpTestkit = groupId %% "akka-http-testkit" % akkaHttpVersion
    lazy val streamTestkit = groupId %% "akka-stream-testkit" % akkaVersion
    lazy val actorTestkit = groupId %% "akka-testkit" % akkaVersion
    private lazy val groupId = "com.typesafe.akka"
    private lazy val akkaVersion = "2.5.12"
    private lazy val akkaHttpVersion = "10.1.1"

  }

}
