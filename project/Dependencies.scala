import sbt._

object Dependencies
{
  val ScalaVersion = "2.13.4"

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % "0.11.2" cross CrossVersion.full

  lazy val scalaTypedHoles = "com.github.cb372" % "scala-typed-holes" % "0.1.6" cross CrossVersion.full

  lazy val fs2Io = "co.fs2" %% "fs2-io" % "2.5.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.3"

  lazy val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
}