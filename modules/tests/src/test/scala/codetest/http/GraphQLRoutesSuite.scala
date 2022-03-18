package codetest.http
import cats.effect.IO
import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import codetest.generators.headlineGen
import codetest.models.{Data, NewsResponse}
import codetest.suite.HttpSuite
import com.example.codetest.repo.NewsRepo
import com.example.codetest.{Headline, NewsModule}
import io.circe.Json
import io.circe.parser.parse
import org.http4s.Method.POST
import org.http4s._
import org.http4s.circe.jsonEncoder
import org.http4s.client.dsl.io._
import org.http4s.syntax.literals._
import org.scalacheck.Gen

object GraphQLRoutesSuite extends HttpSuite {

  implicit val (d, s) = Dispatcher[IO].allocated.unsafeRunSync()

  val mod: NewsModule = new NewsModule() {}

  val query: Json =
    parse(
      """{"operationName":null,"variables":{},"query":"{news {title link }}"}"""
    ).getOrElse(sys.error("Failed to parse to GraphQL query"))

  test("GET items succeeds") {
    forall(Gen.listOf(headlineGen)) { news: List[Headline] =>

      val repo = new NewsRepo[IO] {
        override def fetchAll: IO[List[Headline]] = IO.pure(news)

        override def upsert(headlines: List[Headline]): IO[Int] = ???
      }
      val req    = POST(query, uri"/graphql")
      val routes = mod.httpRoutes(repo)

      expectHttpBodyAndStatus(routes, req)(NewsResponse(Data(news)), Status.Ok)
    }
  }
}
