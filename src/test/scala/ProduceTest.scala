import org.scalatest.funsuite.AnyFunSuite
import MyCodec.*
import fs2.kafka.KafkaProducer
import cats.effect.unsafe.implicits.global

class ProduceTest extends AnyFunSuite {

  test("Produces data") {
    KafkaProducer.stream(producerSettings)
      .evalMap { producer =>
        val record = fs2.kafka.ProducerRecord("test-topic", MyKey("key1"), MyRecord("name2", 20))
        val records = fs2.kafka.ProducerRecords.one(record)
        producer.produce(records)
      }
      .compile
      .drain
      .unsafeRunSync()

  }

}
