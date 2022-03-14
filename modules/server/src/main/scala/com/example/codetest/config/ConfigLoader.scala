package com.example.codetest.config

import cats.effect.Async
import cats.implicits._
import ciris._
import ciris.refined.refTypeConfigDecoder
import com.example.codetest.RefinedCustomTypes.URL
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration.FiniteDuration

object ConfigLoader {
  private[this] def databaseConfig: ConfigValue[Effect, DBConfig] = (
    env("POSTGRES_HOST").as[NonEmptyString],
    env("POSTGRES_PORT").as[UserPortNumber],
    env("POSTGRES_USER").as[NonEmptyString],
    env("POSTGRES_PASSWORD").as[NonEmptyString],
    env("POSTGRES_DATABASE").as[NonEmptyString],
    env("POSTGRES_SCHEMA").as[NonEmptyString],
    env("POSTGRES_DRIVER").as[NonEmptyString]
  ).parMapN(DBConfig.apply)

  private[this] def httpLogConfig: ConfigValue[Effect, LogConfig] = (
    env("HTTP_HEADER_LOG").as[Boolean],
    env("HTTP_BODY_LOG").as[Boolean]
  ).parMapN(LogConfig.apply)

  private[this] def httpServerConfig: ConfigValue[Effect, HttpServerConfig] = (
    env("HTTP_HOST").as[NonEmptyString],
    env("HTTP_PORT").as[UserPortNumber]
  ).parMapN(HttpServerConfig.apply)

  private[this] def scrapeConfig: ConfigValue[Effect, ScrapeConfig] = (
    env("SCRAPE_URL").as[URL],
    env("INTERVAL").as[FiniteDuration]
  ).parMapN(ScrapeConfig.apply)

  def app[F[_]: Async]: F[AppConfig] = (
    databaseConfig,
    httpLogConfig,
    httpServerConfig,
    scrapeConfig
  ).parMapN(AppConfig.apply).load[F]
}
