import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaConsumer, KeyDeserializer, ValueDeserializer}
import cats.effect.{IO, Resource}
import fs2.kafka.vulcan.*
import MyCodec.keyCodec
import MyCodec.valueCodec

class ProcessBinary {

  val avroSettings: AvroSettings[IO] = AvroSettings(SchemaRegistryClientSettings[IO]("http://localhost:8081"))

  val valueDeserializer: Resource[IO, ValueDeserializer[IO, MyRecord]] = AvroDeserializer[MyRecord].forValue(avroSettings)
  val keyDeserializer: Resource[IO, KeyDeserializer[IO, MyKey]] = AvroDeserializer[MyKey].forKey(avroSettings)

  private val consumerSettings: ConsumerSettings[IO, Array[Byte], Array[Byte]] =
    ConsumerSettings[IO, Array[Byte], Array[Byte]]
      .withBootstrapServers("localhost:9092")
      .withGroupId("binary-group")
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

  val myStream: fs2.Stream[IO, KafkaConsumer[IO, Array[Byte], Array[Byte]]] =
    KafkaConsumer.stream(consumerSettings).subscribeTo("test-topic")

  def run(): IO[Unit] =
    valueDeserializer.use{ vd =>
      
      myStream.records.evalTap { x =>
        val k = x.record.key
        val v = x.record.value

        decodeValue(vd, x.record.topic, x.record.headers, v).flatMap {
          case Right(record) => IO.println(s"Decoded record: $record")
          case Left(err)    => IO.println(s"Failed to decode record: $err")
        }
      }.compile.drain
      
    }

  def decodeValue(deserializer: ValueDeserializer[IO, MyRecord], topic: String, headers: fs2.kafka.Headers, bytes: Array[Byte]): IO[Either[Throwable, MyRecord]] =
    deserializer.deserialize(topic, headers, bytes).attempt
}
