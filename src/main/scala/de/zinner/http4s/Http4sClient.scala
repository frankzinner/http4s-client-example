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

  def getReqPagedAttributes[F[_] : Concurrent](client: Client[F])(using logger: Logger[F]): F[PageResponse] = {
    client
      .expect[PageResponse](getReq)(jsonOf[F, PageResponse])
      .map((res: PageResponse) => {
        logger.info("handle attributes get request") >> logger.info(s"Response $res") >> res.pure
      }).flatMap(r => r)
  }

  def postReqAttribute[F[_] : Concurrent](client: Client[F])(using logger: Logger[F]): F[Attribute] = {
    client
      .expect[Attribute](postReq)(jsonOf[F, Attribute])
      .map((res: Attribute) => {
        logger.info("handle attribute post request") >> logger.info(s"Response $res") >> res.pure
      }).flatMap(r => r)
  }

  def handleAttributes[F[_] : Concurrent](client: Client[F])(using logger: Logger[F]): F[Unit] = for {
    _ <- client
          .expect[PageResponse](getReq)(jsonOf[F, PageResponse])
          .flatMap((res: PageResponse) => {
            logger.info("handle attributes get request") >> logger.info(s"Response $res")
          })

    _ <- client
          .expect[Attribute](postReq)(jsonOf[F, Attribute])
          .flatMap((res: Attribute) => {
            logger.info("handle attribute post request") >> logger.info(s"Response $res")
          })
  } yield ().pure

  /*
    override def run[F[_] : Async : Network]: F[Nothing] =

      EmberClientBuilder
        .default[F]
        //      .withLogger(logger)
        .build
        .map { client => {
          val pageResponse: F[PageResponse] = getReqPagedAttributes(client)
          (client, pageResponse)
        }
        }
        .map { case (client, pageResponse) => {
          val attribute: F[Attribute] = postReqAttribute(client)
          (client, pageResponse, attribute)
        }
        }
        .map { case (client, pageResponse, attribute) => {
          val finalHttpApp: HttpApp[F] = Logger.httpApp(true, true)(httpApp);
        }

        }
        .flatMap { case (client, pageResponse, attribute) => {

        }
        }
        .useForever
  */

  def program[F[_] : Async : Network]: F[Unit] = {

    given logger: Logger[F] = Slf4jFactory.create[F].getLogger

    val builder =
      EmberClientBuilder
      .default[F]
      .withLogger(logger)
      .build
      .use((client: Client[F]) => for {
           _ <- handleAttributes(client)
           _ <- getReqPagedAttributes(client)
           _ <- postReqAttribute(client)
        } yield ()
      )
    builder
  }

  override def run: IO[Unit] = program[IO] // forwards to the run method above (Line 88 ff.)

}
