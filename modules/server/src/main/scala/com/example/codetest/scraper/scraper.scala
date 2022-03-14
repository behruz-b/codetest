package com.example.codetest.scraper

import cats.effect._
import cats.implicits._
import com.example.codetest.Headline
import com.example.codetest.RefinedCustomTypes.URL
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

trait Scraper[F[_]] {
  def headlines(): F[List[Headline]]
}

class NYTimes[F[_]: Sync](
  url: URL,
  browser: Browser,
  getDoc: Browser => F[Document]
) extends Scraper[F] {

  def headlines(): F[List[Headline]] =
    for {
      doc    <- getDoc(browser)
      values <- Sync[F].delay(extractValues(doc))
      list = values.map { case (title, link) =>
        val fullUrl =
          if (link.startsWith(url.value)) link else url.value + link
        Headline(title, fullUrl)
      }
    } yield list

  private def extractValues(doc: Document): List[(String, String)] =
    doc >> elementList("a:has(h2)").map { elements =>
      elements.map { e =>
        (e.extract(text("h2")), e.extract(attr("href")))
      }
    }
}

object NYTimes {
  def apply[F[_]: Sync](url: URL): Scraper[F] =
    new NYTimes[F](
      url,
      JsoupBrowser(),
      browser => Sync[F].delay(browser.get(url.value))
    )
}
