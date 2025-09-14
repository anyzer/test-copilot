package serd.test

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.sksamuel.avro4s.SchemaFor
import fs2.kafka.{KafkaProducer, ProducerSettings}
import org.apache.avro.Schema
import org.scalatest.funsuite.AnyFunSuite
import MyCodec.*

class ProduceTest extends AnyFunSuite {

  test("Produces data") {
    val config = new MyConfig("dev")

    val kschema: Schema = SchemaFor[MyKey].schema
    val vschema: Schema = SchemaFor[MyRecord].schema
    println(kschema)
    println(vschema)

    val producerSettings: ProducerSettings[IO, MyKey, MyRecord] =
      ProducerSettings[IO, MyKey, MyRecord](keySerializer, valueSerializer)
        .withBootstrapServers(config.bootstrap)
        .withProperty("auto.register.schemas", "true")

    KafkaProducer.stream(producerSettings)
      .evalMap { producer =>
        val record = fs2.kafka.ProducerRecord(config.topic, MyKey("key1"), MyRecord("GivenName Surname", 20))
        val records = fs2.kafka.ProducerRecords.one(record)
        producer.produce(records)
      }
      .compile
      .drain
//      .unsafeRunSync()

  }

}
