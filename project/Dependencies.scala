import Dependencies.Libraries._
import sbt._

object Dependencies {
  object Versions {
    val cats          = "2.7.0"
    val catsEffect    = "3.3.5"
    val circe         = "0.14.1"
    val http4s        = "0.23.10"
    val log4cats      = "2.2.0"
    val logback       = "1.2.11"
    val scalaCheck    = "1.15.4"
    val scalaTest     = "3.2.11"
    val scalaTestPlus = "3.2.11.0"
    val sangria       = "2.1.5"
    val sttp          = "3.4.2"
    val monix         = "3.4.0"
    val scalaScraper  = "2.2.1"
    val quillJdbc     = "3.16.3"
    val zioMagic      = "0.3.11"
    val postgresql    = "42.3.3"
  }

  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe

    def http4s(artifact: String): ModuleID = "org.http4s" %% artifact % Versions.http4s

    val circeCore    = circe("circe-core")
    val circeGeneric = circe("circe-generic")
    val circeParser  = circe("circe-parser")

    val http4sDsl    = http4s("http4s-dsl")
    val http4sCore   = http4s("http4s-core")
    val http4sServer = http4s("http4s-blaze-server")
    val http4sClient = http4s("http4s-blaze-client")
    val http4sCirce  = http4s("http4s-circe")

    val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

    val log4cats = "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats
    val logback  = "ch.qos.logback" % "logback-classic" % Versions.logback

    val sangria = "org.sangria-graphql" %% "sangria" % Versions.sangria

    val sttp = "com.softwaremill.sttp.client3" %% "core" % Versions.sttp

    val monix = "io.monix" %% "monix" % Versions.monix // not added yet, it gives conflict with cats-effect version

    val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % Versions.scalaScraper

    val quillJdbc  = "io.getquill"          %% "quill-jdbc-zio" % Versions.quillJdbc
    val zioMagic   = "io.github.kitlangton" %% "zio-magic"      % Versions.zioMagic
    val postgresql = "org.postgresql"        % "postgresql"     % Versions.postgresql

    // Test
    val scalaCheck    = "org.scalacheck"    %% "scalacheck"      % Versions.scalaCheck
    val scalaTest     = "org.scalatest"     %% "scalatest"       % Versions.scalaTest
    val scalaTestPlus = "org.scalatestplus" %% "scalacheck-1-15" % Versions.scalaTestPlus

  }

  val circeLibs = Seq(circeCore, circeGeneric, circeParser)

  val catsLibs = Seq(cats, catsEffect)

  val http4sLibs = Seq(http4sDsl, http4sCore, http4sServer, http4sClient, http4sCirce)

  val logLibs = Seq(log4cats, logback)

  val coreLibraries: Seq[ModuleID] = catsLibs ++ circeLibs ++ http4sLibs ++ logLibs ++ Seq(
    sangria,
    sttp,
    scalaScrapper,
    quillJdbc,
    zioMagic,
    postgresql
  )

  val testLibraries = Seq(
    scalaCheck,
    scalaTest,
    scalaTestPlus
  )
}
