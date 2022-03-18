import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"
ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / scalafixDependencies += Libraries.organizeImports

val scalafixCommonSettings = inConfig(IntegrationTest)(scalafixConfigSettings(IntegrationTest))

lazy val server = (project in file("modules/server"))
  .settings(
    name := "code-test",
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
    name := "code-test-test-suite",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    Defaults.itSettings,
//    scalafixCommonSettings,
    libraryDependencies ++= testLibraries ++ Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
    )
  )
  .dependsOn(server)
