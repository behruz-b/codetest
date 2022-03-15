package com.example.codetest.repo

import cats.effect._
import cats.implicits.{toFunctorOps, toTraverseOps}
import com.example.codetest.Headline
import doobie._
import doobie.implicits._
import doobie.postgres.sqlstate
import org.typelevel.log4cats.Logger

trait NewsRepo[F[_]] {
  def fetchAll: F[List[Headline]]

  def upsert(headlines: List[Headline]): F[Int]
}

object NewsRepo {

  def fromTransactor[F[_]: Async: Logger](xa: Transactor[F]): NewsRepo[F] =
    new NewsRepo[F] {
      val select: Fragment = fr"""SELECT * FROM headlines"""

      def insert(headline: Headline): doobie.ConnectionIO[Int] =
        sql"INSERT INTO headlines (title, link) values (${headline.title}, ${headline.link})".update.run

      override def fetchAll: F[List[Headline]] =
        select.query[Headline].to[List].transact(xa)

      override def upsert(headlines: List[Headline]): F[Int] =
        headlines
          .traverse(headline =>
            insert(headline).exceptSqlState { case sqlstate.class23.UNIQUE_VIOLATION =>
              sql"""UPDATE headlines SET title = ${headline.title}, link = ${headline.link}""".update.run
            }
          )
          .transact(xa)
          .map(_.length)
    }

}
