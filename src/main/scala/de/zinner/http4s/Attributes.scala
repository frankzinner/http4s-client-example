package de.zinner.http4s

import cats.syntax.all.*
import de.zinner.http4s.TokenManagement.KeycloakTokenResponse
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.headers.*
import org.http4s.implicits.*

import scala.util.Random

object Attributes {

  case class Langs(abbr: String, display_name: String)

  case class Attribute(technical_name: String, `type`: String, semantic_type: String, unit: String, description: String, langs: Seq[Langs])

  case class PageResponse(total_count: Int, total_pages: Int, current_page: Int, data: Seq[Attribute])

  private def validChar: Char = new Random().between(97, 123).toChar // a-z

  /* Encode a User request */
  private def attr: Json = Attribute(
    s"tec_name_nr_${validChar}",
    "STRING",
    "FREETEXT",
    "AMPERE_HOUR", // this can not be null => compile error or "null" runtime error => must be one of e.g. AMPERE_HOUR ...
    "Some description",
    Seq(Langs("de_DE", "Anzeigename"), Langs("en", "Displayname"))
  ).asJson

  def getReq[F[_]](token: KeycloakTokenResponse): Request[F] = Request[F](
    Method.GET,
    uri"http://localhost:7777/attributes?page=1",
    httpVersion = HttpVersion.`HTTP/1.1`,
    Headers(
      Accept(MediaType.application.json),
      Authorization(Credentials.Token(AuthScheme.Bearer, token.`access_token`))
  ))

  def postReq[F[_]](token: KeycloakTokenResponse) = Request[F](
    Method.POST,
    uri"http://localhost:7777/attributes",
    httpVersion = HttpVersion.`HTTP/1.1`,
    Headers(
      Accept(MediaType.application.json),
      Authorization(Credentials.Token(AuthScheme.Bearer, token.`access_token`))
    )
  ).withEntity(attr) // withEntity(UrlForm("" -> "", "" -> "", ...))

}
