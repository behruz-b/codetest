package com.example.codetest.test.api

import cats.effect.IO
import com.example.codetest.test.utils.logger.NoOp
import org.http4s._
import org.scalatest.Assertion

class RootRoutesSpec extends RootRoutesChecker[IO] {

  test("Return Hello World") {
    def theTest(method: Method): IO[Assertion] = {
      val shouldReturn =
        if (method == Method.GET)
           Status.Ok

      val params =
        s"""
        Params:
          Method: $method
          Should Return: $shouldReturn
      """
      request(method)
        .map(res => assert(res.status == shouldReturn, params))
        .handleError { error =>
          fail(s"Test failed. Error: $error")
        }
    }

    runAssertions(
      theTest(Method.GET)
    )
  }

}
