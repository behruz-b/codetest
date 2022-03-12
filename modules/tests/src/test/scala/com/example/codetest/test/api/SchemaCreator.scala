package com.example.codetest.test.api

import cats.effect.Sync
import doobie.{Fragment, Transactor}

object SchemaCreator {
  def create[F[_]: Sync](
      xa: Transactor[F],
      user: String,
      database: String
  ): F[Unit] = {
    val script = Fragment.const(s"""
       |GRANT CONNECT ON DATABASE $database TO $user;
       |
       |CREATE SCHEMA IF NOT EXISTS news AUTHORIZATION $user;
       |
       |CREATE TABLE news.headline (
       |  link VARCHAR PRIMARY KEY,
       |  title VARCHAR NOT NULL
       |);       
       |""".stripMargin)
    script.update.run.map(_ => ()).transact(xa)
  }
}