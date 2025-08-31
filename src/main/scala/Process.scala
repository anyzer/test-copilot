import cats.effect.{IO, IOApp, Resource}
import fs2.kafka.*
import fs2.kafka.vulcan.*
//import com.sksamuel.avro4s.*
//import org.apache.avro.generic.GenericRecord
import _root_.vulcan.Codec
import _root_.vulcan.generic.*
import fs2.kafka.vulcan.{AvroDeserializer, AvroSettings}
import MyCodec.*

case class MyKey(name: String)
case class MyRecord(name: String, age: Int)

class Process {
  val myStream: fs2.Stream[IO, KafkaConsumer[IO, MyKey, MyRecord]] =
    KafkaConsumer.stream(consumerCashTransSettings).subscribeTo("test-topic")

  def run(): fs2.Stream[IO, CommittableConsumerRecord[IO, MyKey, MyRecord]] =
    myStream.records.evalTap { x => IO.println(x.record) }

}
