package com.example.codetest.config

case class AppConfig(
  dbConfig: DBConfig,
  logConfig: LogConfig,
  serverConfig: HttpServerConfig,
  scrapeConfig: ScrapeConfig
)