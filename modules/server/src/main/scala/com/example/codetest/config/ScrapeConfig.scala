package com.example.codetest.config
import com.example.codetest.RefinedCustomTypes.URL

import scala.concurrent.duration.FiniteDuration

final case class ScrapeConfig(newsPageUrl: URL, interval: FiniteDuration)
