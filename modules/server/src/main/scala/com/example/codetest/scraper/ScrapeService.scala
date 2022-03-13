package com.example.codetest.scraper

import cats.effect._
import cats.implicits._
import com.example.codetest.repo.NewsRepo
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.FiniteDuration

class ScrapeService[F[_]: Async](
    scraper: Scraper[F],
    repo: NewsRepo[F],
    interval: Option[FiniteDuration]
)(implicit L: Logger[F], T: Temporal[F]) {

  def scrape: F[Unit] =
    for {
      headlines <- scraper.headlines()
      count <- repo.upsert(headlines)
      _ <- L.info(s"Upserted record count: $count")
      _ <- interval match {
        case Some(i) => T.sleep(i) *> scrape
        case None    => L.info(s"Stopping scrapper...")
      }
    } yield ()
}
