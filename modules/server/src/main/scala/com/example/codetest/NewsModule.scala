package com.example.codetest

import _root_.sangria.schema._
import cats.effect._
import cats.effect.std.{Dispatcher, Supervisor}
import cats.implicits._
import com.example.codetest.api.{GraphQL, GraphQLRoutes, PlaygroundRoutes}
import com.example.codetest.config.{ConfigLoader, DBConfig, ScrapeConfig}
import com.example.codetest.effects.Background
import com.example.codetest.repo.NewsRepo
import com.example.codetest.sangria.SangriaGraphQL
import com.example.codetest.schema.QueryType
import com.example.codetest.scraper.{NYTimes, ScrapeService, Scraper}
import doobie.hikari._
import doobie.util.ExecutionContexts
import eu.timepit.refined.auto.autoUnwrap
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

import scala.concurrent.ExecutionContext.global

trait NewsModule {

  def transactor[F[_]: Async](
    cfg: DBConfig
  ): Resource[F, HikariTransactor[F]] =
    ExecutionContexts.fixedThreadPool[F](10).flatMap { ce =>
      HikariTransactor.newHikariTransactor(
        cfg.driver,
        s"jdbc:postgresql://${cfg.host}:${cfg.port}/${cfg.database}?currentSchema=${cfg.schema}",
        cfg.user,
        cfg.password,
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

  def scrapeTask[F[_]: Async: Logger: Background](
    repo: NewsRepo[F],
    cfg: ScrapeConfig
  ): F[Unit] =
    for {
      scraper       <- Sync[F].delay(NYTimes[F](cfg.newsPageUrl))
      scrapeService <- Sync[F].delay(new ScrapeService[F](scraper, repo))
      _             <- scrapeService.scrape(cfg.interval)
    } yield ()

  def scrapeService[F[_]: Async: Logger: Background](
    scraper: Scraper[F],
    repo: NewsRepo[F]
  ) = new ScrapeService[F](scraper, repo)

  def tasks[F[_]: Async]: Resource[F, Server] = {
    implicit val log: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
    Supervisor[F].flatMap { implicit sp =>
      Dispatcher[F].flatMap { implicit dispatcher =>
        for {
          cfg <- Resource.eval(ConfigLoader.app[F])
          xa  <- transactor[F](cfg.dbConfig)
          repo = NewsRepo.fromTransactor(xa)
          server <- graphQLServer[F](repo)
          _      <- Resource.eval(scrapeTask[F](repo, cfg.scrapeConfig))
        } yield server
      }
    }
  }

}
