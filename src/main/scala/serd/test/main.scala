package serd.test

import cats.effect.{IO, IOApp}

object HelloWorld extends IOApp.Simple {

  val run: IO[Unit] =
    for {
      config <- IO(new MyConfig("dev"))
      _ <- new ProcessGenericRecordManualDes(config).run()
    } yield ()

}
