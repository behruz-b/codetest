package com.example.codetest.scraper

import cats.effect._
import cats.implicits._
import com.example.codetest.Headline
import com.example.codetest.effects.Background
import com.example.codetest.repo.NewsRepo
import org.typelevel.log4cats.Logger
import retry._
import retry.RetryPolicies._
import retry.RetryDetails._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

class ScrapeService[F[_]: Async: Background: Sleep](
  scraper: Scraper[F],
  repo: NewsRepo[F]
)(implicit L: Logger[F]) {

  def retry[A](fa: F[A]): F[A] = {
    Temporal[F].sleep(1.minute) >> fa
  }

  def logError(action: String)(
    err: Throwable,
    details: RetryDetails
  ): F[Unit] = details match {
    case r: WillDelayAndRetry =>
      Logger[F].error(
        s"Failed to $action. So far we have retried ${r.retriesSoFar} times. Error: ${err.getMessage}"
      )
    case g: GivingUp =>
      Logger[F].error(
        s"Giving up after ${g.totalRetries} retries"
      )

  }

  val retryPolicy: RetryPolicy[F] = limitRetries[F](3) |+| exponentialBackoff[F](3.minutes)

  def scraperProcess: F[List[Headline]] = {
    retryingOnAllErrors[List[Headline]](
      policy = retryPolicy,
      onError = logError("Parse Web Page")
    )(scraper.headlines())
  }

  def scrape(interval: FiniteDuration): F[Unit] =
    for {
      headlines <- scraperProcess
      count     <- repo.upsert(headlines)
      _         <- L.info(s"Upserted record count: $count")
      _         <- Background[F].schedule(scrape(interval), interval)
    } yield ()
}
