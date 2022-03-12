// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package com.example.codetest.schema

import cats.effect._
import cats.effect.std.Dispatcher
import com.example.codetest.repo.NewsRepo
import sangria.schema._


object QueryType {

  def apply[F[_]: Async](implicit dispatcher: Dispatcher[F]): ObjectType[NewsRepo[F], Unit] = {
      ObjectType(
        name = "Query",
        fields = fields(
          Field(
            name = "news",
            fieldType = ListType(NewsType[F]),
            description = Some("Returns all news."),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.fetchAll)
          )
        )
      )
  }

  def schema[F[_]: Async: Dispatcher]: Schema[NewsRepo[F], Unit] =
    Schema(QueryType[F])

}
