package com.example.codetest.schema

import cats.effect._
import com.example.codetest.Headline
import com.example.codetest.repo.NewsRepo
import sangria.schema._

object NewsType {

  def apply[F[_]: Async]: ObjectType[NewsRepo[F], Headline] =
    ObjectType(
      name = "News",
      fieldsFn = () =>
        fields(
          Field(
            name = "title",
            fieldType = StringType,
            resolve = _.value.title
          ),
          Field(
            name = "link",
            fieldType = StringType,
            resolve = _.value.link
          )
        )
    )

}
