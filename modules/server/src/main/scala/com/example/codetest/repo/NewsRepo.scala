package com.example.codetest.repo

import cats.effect._
import cats.implicits._
import com.example.codetest.Headline
import doobie._
import doobie.implicits._
import doobie.postgres.implicits.toPostgresMonadErrorOps
import doobie.postgres.sqlstate
import org.typelevel.log4cats.Logger

trait NewsRepo[F[_]] {
  def fetchAll: F[List[Headline]]

  def upsert(headlines: List[Headline]): F[Int]
}

object NewsRepo {

  def fromTransactor[F[_]: Async: Logger](xa: Transactor[F]): NewsRepo[F] =
    new NewsRepo[F] {
      val select: Fragment = fr"""SELECT * FROM headline"""

      def insert(headline: Headline): doobie.ConnectionIO[Int] =
        sql"INSERT INTO headline (title, link) values (${headline.title}, ${headline.link})".update.run

      def update(headline: Headline): doobie.ConnectionIO[Int] =
        sql"""UPDATE headline SET title = ${headline.title}, link = ${headline.link} where link = ${headline.link}""".update.run

      override def fetchAll: F[List[Headline]] =
        select.query[Headline].to[List].transact(xa)

      override def upsert(headlines: List[Headline]): F[Int] = {
        headlines
          .traverse(headline => insert(headline).onUniqueViolation(HC.rollback *> update(headline)))
          .transact(xa)
          .map(_.length)
      }
    }

}
