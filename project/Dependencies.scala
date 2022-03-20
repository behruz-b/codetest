import Dependencies.Libraries._
import sbt._

object Dependencies {
  object Versions {
    val cats          = "2.7.0"
    val catsEffect    = "3.3.6"
    val circe         = "0.14.1"
    val http4s        = "0.23.10"
    val ciris         = "2.3.2"
    val refined       = "0.9.28"
    val log4cats      = "2.2.0"
    val logback       = "1.2.11"
    val scalaCheck    = "1.15.4"
    val scalaTest     = "3.2.10"
    val scalaTestPlus = "3.2.10.0"
    val sangria       = "3.0.0"
    val sttp          = "3.4.2"
    val monix         = "3.4.0"
    val scalaScraper  = "2.2.1"
    val postgresql    = "42.3.3"
    val doobie        = "1.0.0-RC1"
    val derevo        = "0.13.0"

    val betterMonadicFor = "0.3.1"
    val kindProjector    = "0.13.2"
    val semanticDB       = "4.5.0"
    val organizeImports  = "0.6.0"

  }

  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe

    def http4s(artifact: String): ModuleID = "org.http4s" %% artifact % Versions.http4s

    def ciris(artifact: String): ModuleID = "is.cir" %% artifact % Versions.ciris

    def refined(artifact: String): ModuleID = "eu.timepit" %% artifact % Versions.refined

    def doobie(artifact: String): ModuleID = "org.tpolecat" %% artifact % Versions.doobie

    def sttp(artifact: String): ModuleID = "com.softwaremill.sttp.client3" %% artifact % Versions.sttp

    def sangria(artifact: String, version: String): ModuleID = "org.sangria-graphql" %% artifact % version

    def derevo(artifact: String): ModuleID = "tf.tofu" %% s"derevo-$artifact" % Versions.derevo

    val circeCore    = circe("circe-core")
    val circeGeneric = circe("circe-generic")
    val circeParser  = circe("circe-parser")
    val circeOptics  = circe("circe-optics")

    val doobieCore     = doobie("doobie-core")
    val doobiePostgres = doobie("doobie-postgres")
    val doobieHikari   = doobie("doobie-hikari")

    val http4sDsl    = http4s("http4s-dsl")
    val http4sCore   = http4s("http4s-core")
    val http4sServer = http4s("http4s-blaze-server")
    val http4sClient = http4s("http4s-blaze-client")
    val http4sCirce  = http4s("http4s-circe")

    val sttpHttp4sBackend = sttp("http4s-backend")
    val sttpCirce         = sttp("circe")

    val refinedType = refined("refined")

    val cirisCore    = ciris("ciris")
    val cirisRefined = ciris("ciris-refined")

    val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

    val log4cats = "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats
    val logback  = "ch.qos.logback" % "logback-classic" % Versions.logback

    val sangriaSelf  = sangria("sangria", Versions.sangria)
    val sangriaCirce = sangria("sangria-circe", "1.3.2")

    val derevoCore  = derevo("core")
    val derevoCats  = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % Versions.scalaScraper
    val postgresql    = "org.postgresql"    % "postgresql"    % Versions.postgresql

    val organizeImports = "com.github.liancheng" %% "organize-imports" % Versions.organizeImports

    // Test
    val log4catsNoOp      = "org.typelevel"     %% "log4cats-noop"      % Versions.log4cats
    val refinedScalacheck = "eu.timepit"        %% "refined-scalacheck" % Versions.refined
    val scalaCheck        = "org.scalacheck"    %% "scalacheck"         % Versions.scalaCheck
    val scalaTest         = "org.scalatest"     %% "scalatest"          % Versions.scalaTest
    val scalaTestPlus     = "org.scalatestplus" %% "scalacheck-1-15"    % Versions.scalaTestPlus

  }

  val circeLibs = Seq(circeCore, circeGeneric, circeParser, circeOptics)

  val doobieLibs = Seq(doobieCore, doobiePostgres, doobieHikari)

  val catsLibs = Seq(cats, catsEffect)

  val http4sLibs = Seq(http4sDsl, http4sCore, http4sServer, http4sClient, http4sCirce)

  val sttpLibs = Seq(sttpHttp4sBackend, sttpCirce)

  val logLibs = Seq(log4cats, logback)

  val cirisLibs  = Seq(cirisRefined, cirisCore)
  val derevoLibs = Seq(derevoCore, derevoCats, derevoCirce)

  val coreLibraries: Seq[ModuleID] =
    Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      refinedType,
      sangriaSelf,
      sangriaCirce,
      scalaScrapper,
      postgresql
    ) ++ catsLibs ++ doobieLibs ++ cirisLibs ++ circeLibs ++ http4sLibs ++ sttpLibs ++ logLibs ++ derevoLibs

  val testLibraries = Seq(
    scalaCheck,
    scalaTest,
    scalaTestPlus,
    log4catsNoOp,
    refinedScalacheck
  )
  object CompilerPlugin {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % Versions.kindProjector cross CrossVersion.full
    )
    val semanticDB = compilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % Versions.semanticDB cross CrossVersion.full
    )
  }
}
