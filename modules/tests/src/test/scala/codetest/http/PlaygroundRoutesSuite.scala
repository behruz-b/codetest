package codetest.http

import cats.effect.IO
import cats.effect.std.Dispatcher
import codetest.suite.HttpSuite
import com.example.codetest.Headline
import com.example.codetest.repo.NewsRepo
import org.http4s.Method.GET
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.syntax.literals._

class PlaygroundRoutesSuite extends HttpSuite {

  test("Open Playground html") {
    val repo = new NewsRepo[IO] {
      override def fetchAll: IO[List[Headline]] = ???

      override def upsert(headlines: List[Headline]): IO[Int] = ???
    }
    val req = GET(uri"/playground.html")
    Dispatcher[IO].use { implicit d =>
      val routes = mod.httpRoutes(repo)
      expectHttpStatus(routes, req)(Status.Ok)
    }
  }
}
