package com.example.codetest.scraper

import cats.effect._
import cats.implicits._
import com.example.codetest.effects.Background
import com.example.codetest.repo.NewsRepo
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.FiniteDuration

class ScrapeService[F[_]: Async: Background](
  scraper: Scraper[F],
  repo: NewsRepo[F]
)(implicit L: Logger[F]) {

  def scrape(interval: FiniteDuration): F[Unit] =
    for {
      headlines <- scraper.headlines()
      count     <- repo.upsert(headlines)
      _         <- L.info(s"Upserted record count: $count")
      _         <- Background[F].schedule(scrape(interval), interval)
    } yield ()
}
