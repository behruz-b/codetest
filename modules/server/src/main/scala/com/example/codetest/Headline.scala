package com.example.codetest

import cats.Show
import derevo.cats.show
import derevo.circe.magnolia.encoder
import derevo.derive

import java.time.LocalDateTime

@derive(encoder)
final case class Headline(title: String, link: String, createdAt: LocalDateTime)

object Headline {
  implicit val headlineShow: Show[Headline] = Show.fromToString
}
