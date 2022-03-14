package com.example.codetest
import cats.effect._
import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends NewsModule with IOApp {

  implicit val (d, s) = Dispatcher[IO].allocated.unsafeRunSync()
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] =
    tasks[IO].use { case (server, scraper) =>
      IO.race(server, scraper).map(_.merge)
    }
}
