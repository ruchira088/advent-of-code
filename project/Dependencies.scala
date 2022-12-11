import sbt._

object Dependencies
{
  val ScalaVersion = "2.13.10"

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full

  lazy val fs2IO = "co.fs2" %% "fs2-io" % "3.4.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.14"

  lazy val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
}