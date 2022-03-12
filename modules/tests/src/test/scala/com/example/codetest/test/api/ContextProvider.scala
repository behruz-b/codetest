package com.example.codetest.test.api

import cats.effect.IO

import scala.concurrent.ExecutionContext

trait ContextProvider {
  implicit lazy val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit lazy val contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)
}
