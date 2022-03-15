package com.example.codetest
import cats.effect._
import cats.effect.std.Dispatcher

object Main extends NewsModule with IOApp {

  implicit val (d, s) = Dispatcher[IO].allocated.unsafeRunSync()(cats.effect.unsafe.implicits.global)

  def run(args: List[String]): IO[ExitCode] =
    tasks[IO].use { case (server, scraper) =>
      IO.race(server, scraper).map(_.merge)
    }
}
