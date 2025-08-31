import cats.effect.{IO, IOApp}

object HelloWorld extends IOApp.Simple {

  val run: IO[Unit] = {
    IO.println("Hello, Cats Effect!") >>
      new ProcessBinary().run()
  }


}