package de.zinner.http4s

import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*

object TokenManagement {

  private val realm: String = "product-passport"

  final case class KeycloakTokenResponse(
      `access_token`: String,
      `expires_in`: Long,
      `refresh_expires_in`: Long,
      `refresh_token`: String,
      `token_type`: String,
      `not-before-policy`: Long,
      `session_state`: String, // UUID
      `scope`: String
  )

  def postReq2CreateToken[F[_]] = Request[F](
    Method.POST,
    uri"http://localhost:7070/realms/product-distributor/protocol/openid-connect/token",
    httpVersion = HttpVersion.`HTTP/1.1`,
    Headers(
      `Content-Type`(MediaType.application.`x-www-form-urlencoded`, Charset.`UTF-8`),
      Accept(MediaType.application.json),
    )
  ).withEntity(UrlForm(
    "grant_type" -> "password",
    "client_id" -> "bpe-login",
    "username" -> "initial.user@bpe.com",
    "password" -> "#Password123#"
  ))

}
