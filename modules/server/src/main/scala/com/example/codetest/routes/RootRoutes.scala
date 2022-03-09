package com.example.codetest.routes
import cats.effect.{Async, Sync}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

object RootRoutes {
  def apply[F[_]: Async: Sync: Logger]: RootRoutes[F] =
    new RootRoutes[F]
}

class RootRoutes[F[_]: Async: Logger] {
  implicit object dsl extends Http4sDsl[F]; import dsl._

  private[this] val helloRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok("Hello World!")
  }

  val routes: HttpRoutes[F] = helloRoutes



}
