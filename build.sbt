import Dependencies.*

lazy val root =
  (project in file("."))
    .settings(
      name := "advent-of-code",
      organization := "com.ruchij",
      scalaVersion := Dependencies.ScalaVersion,
      version := "0.0.1",
      libraryDependencies ++= rootDependencies ++ rootTestDependencies.map(_ % Test),
      libraryDependencies += "com.github.sbt.junit" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
      scalacOptions ++= Seq("-Xlint", "-feature"),
      addCompilerPlugin(kindProjector)
    )

lazy val rootDependencies =
  Seq(fs2IO)

lazy val rootTestDependencies =
  Seq(scalaTest, junitJupiterApi, pegdown)

addCommandAlias("testWithCoverage", "; coverage; test; coverageReport")
