package com.example.codetest

import _root_.sangria.schema._
import cats.effect._
import cats.effect.std.Dispatcher
import cats.implicits._
import com.example.codetest.RefinedCustomTypes._
import com.example.codetest.api.{GraphQL, GraphQLRoutes, PlaygroundRoutes}
import com.example.codetest.config.{ConfigLoader, DBConfig, ScrapeConfig}
import com.example.codetest.repo.NewsRepo
import com.example.codetest.sangria.SangriaGraphQL
import com.example.codetest.schema.QueryType
import com.example.codetest.scraper.{NYTimes, ScrapeService, Scraper}
import doobie.hikari._
import doobie.util.ExecutionContexts
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.FiniteDuration

trait NewsModule {

  def transactor[F[_]: Async](
    cfg: DBConfig
  ): Resource[F, HikariTransactor[F]] =
    ExecutionContexts.fixedThreadPool[F](10).flatMap { ce =>
      HikariTransactor.newHikariTransactor(
        cfg.driver.value,
        s"jdbc:postgresql://${cfg.host.value}:${cfg.port.value}/${cfg.database.value}?currentSchema=${cfg.schema.value}",
        cfg.user.value,
        cfg.password.value,
        ce
      )
    }

  def server[F[_]: Async: Temporal](
    routes: HttpRoutes[F]
  ): Resource[F, Server] =
    BlazeServerBuilder[F]
      .withExecutionContext(global)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(routes.orNotFound)
      .resource

  def graphQL[F[_]: Async: Dispatcher](
    repo: NewsRepo[F]
  ): GraphQL[F] =
    SangriaGraphQL[F](
      Schema(
        query = QueryType[F]
      ),
      repo.pure[F]
    )

  def graphQLServer[F[_]: Async: Temporal: Logger: Dispatcher](
    repo: NewsRepo[F]
  ): Resource[F, Server] = {
    val rts = httpRoutes(repo)
    server[F](rts)
  }

  def httpRoutes[F[_]: Async: Temporal: Dispatcher](
    repo: NewsRepo[F]
  ): HttpRoutes[F] = {
    val gql = graphQL[F](repo)
    GraphQLRoutes[F](gql) <+> PlaygroundRoutes()
  }

  def scraper[F[_]: Async](url: URL): Scraper[F] =
    NYTimes[F](url)

  def scrapeTask[F[_]: Async: Logger: Temporal](
    repo: NewsRepo[F],
    cfg: ScrapeConfig
  ): F[Unit] =
    for {
      scraper <- scraper(cfg.newsPageUrl).pure[F]
      _       <- scrapeService(scraper, repo, Some(cfg.interval)).scrape
    } yield ()

  def scrapeService[F[_]: Async: Logger: Temporal](
    scraper: Scraper[F],
    repo: NewsRepo[F],
    interval: Option[FiniteDuration]
  ) =
    new ScrapeService[F](scraper, repo, interval)

  def tasks[F[_]: Temporal: Async: Dispatcher]: Resource[F, (F[ExitCode], F[ExitCode])] = {
    implicit val log: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    for {
      cfg <- Resource.eval(ConfigLoader.app[F])
      xa  <- transactor[F](cfg.dbConfig)
      repo = NewsRepo.fromTransactor(xa)
      server = graphQLServer[F](repo)
        .use(_ =>
          Async[F]
            .async_((_: Either[Throwable, Nothing] => Unit) => ())
            .as(ExitCode.Error) <* log.info("HTTP server stopped")
        )
      scraper = scrapeTask[F](repo, cfg.scrapeConfig).as(ExitCode.Error)
    } yield (server, scraper)
  }

}
