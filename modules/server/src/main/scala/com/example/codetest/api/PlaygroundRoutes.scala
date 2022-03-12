package com.example.codetest.api

import cats.effect.Sync
import cats.effect.kernel.Async
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.implicits._
import org.http4s.{HttpRoutes, StaticFile}

object PlaygroundRoutes {

  def apply[F[_]: Sync: Async]: HttpRoutes[F] = {
    object dsl extends Http4sDsl[F];
    import dsl._
    HttpRoutes.of[F] {

      case GET -> Root / "playground.html" =>
        StaticFile
          .fromResource[F]("/assets/playground.html")
          .getOrElseF(NotFound())

      case _ =>
        PermanentRedirect(Location(uri"/playground.html"))
    }
  }
}
