package com.example.codetest
import cats.effect.std.Console
import cats.effect.{Async, ExitCode}
import cats.implicits._
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.Logger

import scala.concurrent.ExecutionContext.global

object Server {

  def run[F[_]: Async: Logger: Console]: F[ExitCode] =
    for {
      httpAPI  <- HttpApi[F]
      _ <- server[F](httpAPI.httpApp)
    } yield ExitCode.Success

  private[this] def server[F[_]: Async](httpApp: HttpApp[F]): F[Unit] =
    BlazeServerBuilder[F]
      .withExecutionContext(global)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

}
