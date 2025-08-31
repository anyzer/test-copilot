import vulcan.Codec
import _root_.vulcan.generic.*
import cats.effect.IO
import fs2.kafka.vulcan.{AvroDeserializer, AvroSerializer, AvroSettings, SchemaRegistryClientSettings}
import cats.effect.Resource
import fs2.kafka.*


object MyCodec {
  implicit val valueCodec: Codec[MyRecord] = Codec.derive[MyRecord]
  implicit val keyCodec: Codec[MyKey] = Codec.derive[MyKey]

  val avroSettings: AvroSettings[IO] = AvroSettings(SchemaRegistryClientSettings[IO]("http://localhost:8081"))

  implicit val valueDeserializer: Resource[IO, ValueDeserializer[IO, MyRecord]] = AvroDeserializer[MyRecord].forValue(avroSettings)
  implicit val keyDeserializer: Resource[IO, KeyDeserializer[IO, MyKey]] = AvroDeserializer[MyKey].forKey(avroSettings)
  implicit val valueSerializer: Resource[IO, ValueSerializer[IO, MyRecord]] = AvroSerializer[MyRecord].forValue(avroSettings)
  implicit val keySerializer: Resource[IO, KeySerializer[IO, MyKey]] = AvroSerializer[MyKey].forKey(avroSettings)

  val consumerCashTransSettings: ConsumerSettings[IO, MyKey, MyRecord] =
    ConsumerSettings[IO, MyKey, MyRecord](keyDeserializer, valueDeserializer)
      .withAutoOffsetReset(AutoOffsetReset.Latest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("MyRecord-Group")
      .withEnableAutoCommit(false)

  val producerSettings: ProducerSettings[IO, MyKey, MyRecord] =
    ProducerSettings[IO, MyKey, MyRecord](keySerializer, valueSerializer)
      .withBootstrapServers("localhost:9092")

}
