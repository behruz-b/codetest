import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val server = (project in file("modules/server"))
  .settings(
    name := "code-test",
    libraryDependencies ++= coreLibraries
  )

lazy val tests = project
  .in(file("modules/tests"))
  .configs(IntegrationTest)
  .settings(
    name := "code-test-test-suite",
    Defaults.itSettings,
    libraryDependencies ++= testLibraries ++ testLibraries.map(_ % Test)
  )
  .dependsOn(server)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
