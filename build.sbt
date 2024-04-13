import Dependencies._

lazy val root =
  (project in file("."))
    .settings(
      name := "advent-of-code",
      organization := "com.ruchij",
      scalaVersion := Dependencies.ScalaVersion,
      version := "0.0.1",
      libraryDependencies ++= rootDependencies ++ rootTestDependencies.map(_ % Test),
      scalacOptions ++= Seq("-Xlint", "-feature"),
      addCompilerPlugin(kindProjector)
    )

lazy val rootDependencies =
  Seq(fs2IO)

lazy val rootTestDependencies =
  Seq(scalaTest, junit, junitInterface, pegdown)

addCommandAlias("testWithCoverage", "; coverage; test; coverageReport")
