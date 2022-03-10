package com.example.codetest

import cats.effect._
import com.example.codetest.config.{AppConfig, LogConfig}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.{Router, middleware}
import org.typelevel.log4cats.Logger

object HttpApi {
  def apply[F[_]: Async: Logger](conf: LogConfig)(implicit F: Sync[F]): F[HttpApi[F]] =
    F.delay(
      new HttpApi[F](conf)
    )
}

final class HttpApi[F[_]: Async: Logger] private(conf: LogConfig) {
  private[this] val root: String = "/"

  private[this] val rootRoutes: HttpRoutes[F] = RootRoutes[F].routes

  private[this] val loggedRoutes: HttpRoutes[F] => HttpRoutes[F] = http =>
    middleware.Logger.httpRoutes(logHeaders = conf.httpHeader, logBody = conf.httpBody)(http)

  val httpApp: HttpApp[F] =
    loggedRoutes(
      Router(
        root -> rootRoutes
      )
    ).orNotFound
}
