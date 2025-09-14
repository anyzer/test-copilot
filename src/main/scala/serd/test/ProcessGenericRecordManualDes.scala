package serd.test

import cats.effect.{IO, Resource}
import com.sksamuel.avro4s.SchemaFor
import fs2.kafka.*
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.DecoderFactory

class ProcessGenericRecordManualDes(config: MyConfig) {

  private val consumerSettings: ConsumerSettings[IO, Array[Byte], Array[Byte]] =
    ConsumerSettings[IO, Array[Byte], Array[Byte]]
      .withBootstrapServers(config.bootstrap)
      .withGroupId("binary-gr-group")
      .withAutoOffsetReset(AutoOffsetReset.Earliest)

  private val schema: Schema = SchemaFor[MyRecord].schema

  val myStream: fs2.Stream[IO, CommittableConsumerRecord[IO, Array[Byte], Array[Byte]]] =
    KafkaConsumer.stream(consumerSettings).subscribeTo(config.topic).records

  def run(): IO[Unit] =
    myStream.evalTap { x =>
      val record = decodeValue(schema, x.record.value)
      IO.println(s"Decoded GenericRecord Manual without Registry: $record")
    }.compile.drain

  def decodeValue(schema: Schema, bytes: Array[Byte]): GenericRecord = {
    val reader = new GenericDatumReader[GenericRecord](schema)
    val decoder = DecoderFactory.get().binaryDecoder(bytes, null)
    reader.read(null, decoder)
  }

}
