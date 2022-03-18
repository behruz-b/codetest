import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(name := "code-test").aggregate(server, tests)

lazy val server = (project in file("modules/server"))
  .settings(
    name := "code-test",
    scalafmtOnCompile := true,
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    libraryDependencies ++= coreLibraries
  )

ThisBuild / scalacOptions --= Seq(
  "-Xlint:by-name-right-associative",
  "-Xlint:nullary-override",
  "-Xlint:unsound-match",
  "-Yno-adapted-args"
)

lazy val tests = (project in file("modules/tests"))
  .configs(IntegrationTest)
  .settings(
    name := "code-test-suite",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    Defaults.itSettings,
    coverageEnabled := true,
    libraryDependencies ++= testLibraries
  )
  .dependsOn(server)
