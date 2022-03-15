package com.example.codetest
import cats.effect._

object Main extends NewsModule with IOApp {

  def run(args: List[String]): IO[ExitCode] =
    tasks[IO].useForever.as(ExitCode.Success)
}
