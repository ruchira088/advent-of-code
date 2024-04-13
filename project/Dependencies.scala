import sbt._

object Dependencies
{
  val ScalaVersion = "2.13.13"

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full

  lazy val fs2IO = "co.fs2" %% "fs2-io" % "3.10.2"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.18"

  lazy val junit = "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.2"

  lazy val junitInterface = "com.github.sbt" % "junit-interface" % "0.13.3"

  lazy val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
}