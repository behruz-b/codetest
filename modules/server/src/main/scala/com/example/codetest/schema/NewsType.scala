package com.example.codetest.schema

import cats.effect._
import com.example.codetest.Headline
import com.example.codetest.repo.NewsRepo
import org.joda.time.LocalDateTime
import sangria.schema._

import java.time.format.DateTimeFormatter
import javax.swing.text.DateFormatter

object NewsType {

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def apply[F[_]: Async]: ObjectType[NewsRepo[F], Headline] =
    ObjectType(
      name = "News",
      fieldsFn = () =>
        fields(
          Field(
            name = "ttl",
            fieldType = StringType,
            resolve = _.value.title
          ),
          Field(
            name = "link",
            fieldType = StringType,
            resolve = _.value.link
          ),
          Field(
            name = "date",
            fieldType = StringType,
            resolve = _.value.createdAt.format(formatter)
          )
        )
    )

}
