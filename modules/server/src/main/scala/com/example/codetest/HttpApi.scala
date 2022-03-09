package com.example.codetest

import cats.effect._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.{Router, middleware}
import org.typelevel.log4cats.Logger

object HttpApi {
  def apply[F[_]: Async: Logger](implicit F: Sync[F]): F[HttpApi[F]] =
    F.delay(
      new HttpApi[F]
    )
}

final class HttpApi[F[_]: Async: Logger] private {
  private[this] val root: String = "/"

  private[this] val rootRoutes: HttpRoutes[F] = RootRoutes[F].routes

  private[this] val loggedRoutes: HttpRoutes[F] => HttpRoutes[F] = http =>
    middleware.Logger.httpRoutes(logHeaders = true, logBody = true)(http)

  val httpApp: HttpApp[F] =
    loggedRoutes(
      Router(
        root -> rootRoutes
      )
    ).orNotFound
}
