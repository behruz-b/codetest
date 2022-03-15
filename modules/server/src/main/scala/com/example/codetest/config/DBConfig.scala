package com.example.codetest.config

import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString

case class DBConfig(
  host: NonEmptyString,
  port: UserPortNumber,
  user: NonEmptyString,
  password: NonEmptyString,
  database: NonEmptyString,
  schema: NonEmptyString,
  driver: NonEmptyString
)
