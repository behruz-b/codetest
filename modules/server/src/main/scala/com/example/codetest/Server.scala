package com.example.codetest
import cats.effect.std.Console
import cats.effect.{Async, ExitCode}
import cats.implicits._
import com.example.codetest.config.{ConfigLoader, HttpServerConfig}
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.Logger

import scala.concurrent.ExecutionContext.global

object Server {

  def run[F[_]: Async: Logger: Console]: F[ExitCode] =
    for {
      conf    <- ConfigLoader.app[F]
      httpAPI <- HttpApi[F](conf.logConfig)
      _       <- server[F](httpAPI.httpApp, conf.serverConfig)
    } yield ExitCode.Success

  private[this] def server[F[_]: Async](httpApp: HttpApp[F], serverConfig: HttpServerConfig): F[Unit] =
    BlazeServerBuilder[F]
      .withExecutionContext(global)
      .bindHttp(serverConfig.port.value, serverConfig.host.value)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

}
