package de.zinner.http4s

import cats.syntax.all.*
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.headers.Accept
import org.http4s.implicits.*

object Attributes {

  case class Langs(abbr: String, display_name: String)

  case class Attribute(technical_name: String, `type`: String, semantic_type: String, unit: String, description: String, usage: Int, langs: Seq[Langs])

  case class PageResponse(total_count: Int, total_pages: Int, current_page: Int, data: Seq[Attribute])

  /* Encode a User request */
  private def attr: Json = Attribute(
    "one",
    "STRING",
    "FREETEXT",
    "AMPERE_HOUR", // this can not be null => compile error or "null" runtime error => must be one of e.g. AMPERE_HOUR ...
    "Some description",
    1,
    Seq(Langs("de_DE", "Anzeigename"))
  ).asJson

  def getReq[F[_]]: Request[F] = Request[F](
    Method.GET,
    uri"http://localhost:7777/attributes?page=1",
    httpVersion = HttpVersion.`HTTP/1.1`,
    Headers(
      Accept(MediaType.application.json),
    )
  )

  def postReq[F[_]] = Request[F](
    Method.POST,
    uri"http://localhost:7777/attributes",
    httpVersion = HttpVersion.`HTTP/1.1`,
    Headers(
      Accept(MediaType.application.json),
    )
  ).withEntity(attr) // withEntity(UrlForm("" -> "", "" -> "", ...))

}
