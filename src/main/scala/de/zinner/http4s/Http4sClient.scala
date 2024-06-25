package de.zinner.http4s

import cats.effect.{Async, Concurrent, IO, IOApp}
import cats.syntax.all.*
import fs2.io.net.Network
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

object Http4sClient extends IOApp.Simple {

  import Attributes.*
  import TokenManagement.*

  def postReqCreateToken[F[_] : Concurrent](client: Client[F])(using logger: Logger[F]): F[KeycloakTokenResponse] = {
    client
      .expect[KeycloakTokenResponse](postReq2CreateToken)(jsonOf[F, KeycloakTokenResponse])
      .map { (res: KeycloakTokenResponse) => logger.info("create token") >> res.pure
      }.flatMap(r => r)
  }

  def getReqPagedAttributes[F[_] : Concurrent](client: Client[F], token: KeycloakTokenResponse)(using logger: Logger[F]): F[PageResponse] = {
    client
      .expect[PageResponse](getReq(token))(jsonOf[F, PageResponse])
      .map((res: PageResponse) => {
        logger.info("handle attributes get request") >> logger.info(s"Response $res") >> res.pure
      }).flatMap(r => r)
  }

  def postReqAttribute[F[_] : Concurrent](client: Client[F], token: KeycloakTokenResponse)(using logger: Logger[F]): F[Attribute] = {
    client
      .expect[Attribute](postReq(token))(jsonOf[F, Attribute])
      .map((res: Attribute) => {
        logger.info("handle attribute post request") >> logger.info(s"Response $res") >> res.pure
      }).flatMap(r => r)
  }

  def handleAttributes[F[_] : Concurrent](client: Client[F], token: KeycloakTokenResponse)(using logger: Logger[F]): F[Unit] = for {
    _ <- client
          .expect[PageResponse](getReq(token))(jsonOf[F, PageResponse])
          .flatMap((res: PageResponse) => {
            logger.info("handle attributes get request") >> logger.info(s"Response $res")
          })

    _ <- client
          .expect[Attribute](postReq(token))(jsonOf[F, Attribute])
          .flatMap((res: Attribute) => {
            logger.info("handle attribute post request") >> logger.info(s"Response $res")
          })
  } yield ().pure

  def program[F[_] : Async : Network]: F[Unit] = {

    given logger: Logger[F] = Slf4jFactory.create[F].getLogger

    val builder =
      EmberClientBuilder
      .default[F]
      .withLogger(logger)
      .build
      .use((client: Client[F]) => for {
           token <- postReqCreateToken(client)
           _ <- handleAttributes(client, token)
           _ <- getReqPagedAttributes(client, token)
           _ <- postReqAttribute(client, token)
        } yield ()
      )
    builder
  }

  override def run: IO[Unit] = program[IO] // forwards to the run method above (Line 56 ff.)

}
