package com.example.codetest.config
import com.example.codetest.refined.CustomTypes.URL

import scala.concurrent.duration.FiniteDuration

final case class ScrapeConfig(newsPageUrl: URL, interval: FiniteDuration)
