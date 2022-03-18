package codetest.suite

import cats.effect.IO
import com.example.codetest.NewsModule
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait HttpSuite extends AnyFunSuite with ScalaCheckDrivenPropertyChecks {

  val mod: NewsModule = new NewsModule() {}

  def expectHttpBodyAndStatus[A: Encoder](routes: HttpRoutes[IO], req: Request[IO])(
    expectedBody: A,
    expectedStatus: Status
  ): IO[Assertion] =
    routes.run(req).value.flatMap {
      case Some(resp) =>
        resp.asJson.map { json =>
          assert(resp.status == expectedStatus && json.dropNullValues == expectedBody.asJson.dropNullValues)
        }
      case None => IO.pure(fail("route not found"))
    }

  def expectHttpStatus(routes: HttpRoutes[IO], req: Request[IO])(expectedStatus: Status): IO[Assertion] =
    routes.run(req).value.map {
      case Some(resp) => assert(resp.status == expectedStatus)
      case None       => fail("route not found")
    }

  def expectHttpFailure(routes: HttpRoutes[IO], req: Request[IO]): IO[Assertion] =
    routes.run(req).value.attempt.map {
      case Left(_)  => succeed
      case Right(_) => fail("expected a failure")
    }

}