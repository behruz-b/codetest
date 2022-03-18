package com.example.codetest

import derevo.cats.show
import derevo.circe.magnolia.encoder
import derevo.derive

@derive(encoder, show)
final case class Headline(title: String, link: String)