import cats.effect.IO
import fs2.kafka.*
import MyCodec.*

case class MyKey(name: String)
case class MyRecord(name: String, age: Int)

class Process {
  val myStream: fs2.Stream[IO, KafkaConsumer[IO, MyKey, MyRecord]] =
    KafkaConsumer.stream(consumerCashTransSettings).subscribeTo("test-topic")

  def run(): fs2.Stream[IO, CommittableConsumerRecord[IO, MyKey, MyRecord]] =
    myStream.records.evalTap { x => IO.println(x.record) }

}
