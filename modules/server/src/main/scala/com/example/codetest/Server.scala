package com.example.codetest
import cats.effect.std.Console
import cats.effect.{Async, ExitCode}
import cats.implicits._
import org.typelevel.log4cats.Logger

object Server {

  def run[F[_]: Async: Logger: Console]: F[ExitCode] = ???

}
