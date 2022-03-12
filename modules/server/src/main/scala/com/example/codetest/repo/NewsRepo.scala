package com.example.codetest.repo

import cats.effect.Sync
import cats.implicits._
import com.example.codetest.Headline
import doobie._
import doobie.implicits._
import doobie.quill.DoobieContext
import io.getquill.{idiom => _, _}
import org.typelevel.log4cats.Logger

trait NewsRepo[F[_]] {
  def fetchAll: F[List[Headline]]

  def upsert(headlines: List[Headline]): F[Int]
}

object NewsRepo {

  def fromTransactor[F[_]: Sync: Logger](xa: Transactor[F]): NewsRepo[F] =
    new NewsRepo[F] {
      val dc = new DoobieContext.Postgres(Literal)

      import dc._

      def fetchAll: F[List[Headline]] = {
        val q = quote(query[Headline])
        Logger[F].debug(s"NewsRepo.fetchAll") *> run(q).transact(xa)
      }

      override def upsert(headlines: List[Headline]): F[Int] = {
        val q = quote {
          liftQuery(headlines).foreach(e =>
            query[Headline]
              .insertValue(e)
              .onConflictUpdate(_.link)((t, e) => t.title -> e.title)
          )
        }
        run(q).transact(xa).map(_.length)
      }
    }

}
