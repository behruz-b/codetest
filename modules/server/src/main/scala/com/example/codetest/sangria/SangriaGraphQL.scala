package com.example.codetest.sangria

import _root_.sangria.ast._
import _root_.sangria.execution._
import _root_.sangria.marshalling.circe._
import _root_.sangria.parser.{QueryParser, SyntaxError}
import _root_.sangria.schema._
import _root_.sangria.validation._
import cats.effect._
import cats.implicits._
import com.example.codetest.api.GraphQL
import io.circe.optics.JsonPath.root
import io.circe.{Json, JsonObject}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

/** A GraphQL implementation based on Sangria. */
object SangriaGraphQL {

  // Some circe lenses
  private val queryStringLens               = root.query.string
  private val operationNameLens             = root.operationName.string
  private val variablesLens                 = root.variables.obj
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  // Format a SyntaxError as a GraphQL `errors`
  private def formatSyntaxError(e: SyntaxError): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "message" -> Json.fromString(e.getMessage),
          "locations" -> Json.arr(
            Json.obj(
              "line"   -> Json.fromInt(e.originalError.position.line),
              "column" -> Json.fromInt(e.originalError.position.column)
            )
          )
        )
      )
    )

  // Format a WithViolations as a GraphQL `errors`
  private def formatWithViolations(e: WithViolations): Json =
    Json.obj("errors" -> Json.fromValues(e.violations.map {
      case v: AstNodeViolation =>
        Json.obj(
          "message" -> Json.fromString(v.errorMessage),
          "locations" -> Json.fromValues(
            v.locations.map(loc =>
              Json.obj(
                "line"   -> Json.fromInt(loc.line),
                "column" -> Json.fromInt(loc.column)
              )
            )
          )
        )
      case v => Json.obj("message" -> Json.fromString(v.errorMessage))
    }))

  // Format a String as a GraphQL `errors`
  private def formatString(s: String): Json =
    Json.obj("errors" -> Json.arr(Json.obj("message" -> Json.fromString(s))))

  // Format a Throwable as a GraphQL `errors`
  private def formatThrowable(e: Throwable): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "class"   -> Json.fromString(e.getClass.getName),
          "message" -> Json.fromString(e.getMessage)
        )
      )
    )

  // Partially-applied constructor
  def apply[F[_]] = new Partial[F]
  final class Partial[F[_]] {

    // The rest of the constructor
    def apply[A](
      schema: Schema[A, Unit],
      userContext: F[A]
    )(implicit
      F: Async[F]
    ): GraphQL[F] =
      new GraphQL[F] {

        // Destruct `request` and delegate to the other overload.
        def query(request: Json): F[Either[Json, Json]] = {
          val queryString   = queryStringLens.getOption(request)
          val operationName = operationNameLens.getOption(request)
          val variables =
            variablesLens.getOption(request).getOrElse(JsonObject())
          queryString match {
            case Some(qs) => query(qs, operationName, variables)
            case None =>
              fail(
                formatString("No 'query' property was present in the request.")
              )
          }
        }

        // Parse `query` and execute.
        def query(
          query: String,
          operationName: Option[String],
          variables: JsonObject
        ): F[Either[Json, Json]] =
          QueryParser.parse(query) match {
            case Success(ast) =>
              exec(schema, userContext, ast, operationName, variables)
            case Failure(e @ SyntaxError(_, _, _)) =>
              fail(formatSyntaxError(e))
            case Failure(e) => fail(formatThrowable(e))
          }

        // Lift a `Json` into the error side of our effect.
        def fail(j: Json): F[Either[Json, Json]] =
          F.pure(j.asLeft)

        // Execute a GraphQL query with Sangria, lifting into IO for safety and sanity.
        def exec(
          schema: Schema[A, Unit],
          userContext: F[A],
          query: Document,
          operationName: Option[String],
          variables: JsonObject
        ): F[Either[Json, Json]] =
          userContext
            .flatMap { ctx =>
              F.async_ { (cb: Either[Throwable, Json] => Unit) =>
                Executor
                  .execute(
                    schema = schema,
                    queryAst = query,
                    userContext = ctx,
                    variables = Json.fromJsonObject(variables),
                    operationName = operationName,
                    exceptionHandler = ExceptionHandler { case (_, e) ???
                      HandledException(e.getMessage)
                    }
                  )
                  .onComplete {
                    case Success(value) => cb(Right(value))
                    case Failure(error) => cb(Left(error))
                  }
              }
            }
            .attempt
            .flatMap {
              case Right(json)               => F.pure(json.asRight)
              case Left(err: WithViolations) => fail(formatWithViolations(err))
              case Left(err)                 => fail(formatThrowable(err))
            }
      }
  }
}
