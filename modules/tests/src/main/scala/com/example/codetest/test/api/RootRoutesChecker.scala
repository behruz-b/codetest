package com.example.codetest.test.api

import cats.effect.{Async, Sync}
import com.example.codetest.RootRoutes
import com.example.codetest.test.utils.TestEnv
import org.http4s
import org.http4s.{Method, Response}
import org.http4s.implicits._
import org.typelevel.log4cats.Logger

class RootRoutesChecker[F[_]: Async: Logger](implicit F: Sync[F]) extends TestEnv {

  def request(method: Method): F[Response[F]] =
    RootRoutes[F].routes.orNotFound(http4s.Request[F](method = method, uri = uri"/graphql"))

}
