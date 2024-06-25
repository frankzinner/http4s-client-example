package de.zinner.http4s

import cats.Parallel
import cats.effect.*
import cats.effect.IO.Par
import cats.effect.implicits.*
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.syntax.apply.*
import cats.syntax.parallel.*

import scala.concurrent.duration.*

object Playground extends IOApp.Simple {

  extension [A](io: IO[A]) {
    def deb: IO[A] = {
      io
        .map { (a: A) => val t = Thread.currentThread().getName; (a, t) }
        .map { case (a: A, t: String) => println(s"[$t] $a"); (a, t) }
        .map { case (a: A, _) => a }
    }
  }

  val program: IO[Unit] = task()

  // shorthand syntax
  val parallelism: IO[String] = (IO.pure(42).deb, IO.pure("Scala").deb).parMapN((num, str) => s"My goal in life is $num and $str")


  private def task(): IO[Unit] = {
    val result = for {
     fib1 <- IO.pure("Starting Scala fiber").deb >> IO(Thread.sleep(2.seconds.toMillis)).onCancel(IO("I've been canceled!").deb.void).start
//   fib1 <- IO.pure("Scala fiber").deb >> IO.raiseError(new RuntimeException("Boom!")).start

      _ <- fib1.cancel
      res1 <- fib1.join // await result res

//      _ <- /*IO("sleeping").deb >>*/ IO.sleep(2.seconds)

      _ <- IO("Starting 10 fibers").deb >> IO( (1 to 10).map(i => IO.println(s"fib#[$i]") >> IO(i).deb.start).foreach(_)) >> IO("finished").deb

      fib2 <- IO("Sleeping beauty").deb >> IO(Thread.sleep(2.seconds.toMillis)).deb.onCancel(IO("got kissed :-)").deb.void).start
      _ <- fib2.cancel
      res2 <- fib2.join // await result _

      _ <- IO("la la la").deb >> IO.sleep(0.5.seconds) >> IO("continue la la la").deb
    } yield res1

    result flatMap {
      case Succeeded(_) => IO(">> Succeeded").deb.void
      case Canceled()   => IO(">> Canceled").deb.void
      case Errored(ex: Throwable)  => IO(s">> Errored with exception ${ex.getMessage}").deb.void
    }
  }


  override def run: IO[Unit] = program


}
