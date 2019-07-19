package fr.xebia.ldi.crocodile.datagen

import java.time.LocalDate
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import fr.xebia.ldi.crocodile.common._
import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Gender.{F, M}
import fr.xebia.ldi.crocodile.common.model._
import fr.xebia.ldi.crocodile.datagen.config.CrocoConf
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serde
import org.slf4j.LoggerFactory
import pureconfig.generic.auto._
import pureconfig.loadConfig

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

/**
  * Created by loicmdivad.
  */
object Main extends App with CommonSerdes {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("crocodile")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val conf: CrocoConf = loadConfig[CrocoConf]("crocodile").right.get

  implicit def getSerde = () => new GenericAvroSerde()

  val linkSerde: Serde[Link] = specificSerde[Link]
  val pageviewSerde: Serde[Click] = specificSerde[Click]
  val purchaseSerde: Serde[Purchase] = specificSerde[Purchase]
  val clientKeySerde: Serde[ClientKey] = specificSerde[ClientKey]

  linkSerde.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, false)
  purchaseSerde.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, false)
  clientKeySerde.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, true)
  pageviewSerde.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, false)

  val links = Vector(
    Link(Client("foo@gmail.com", M, LocalDate.parse("1993-06-24")), url = UUID.randomUUID().toString),
    Link(Client("bar@gmail.com", F, LocalDate.parse("1993-06-24")), url = UUID.randomUUID().toString),
    Link(Client("baz@gmail.com", M, LocalDate.parse("1993-06-24")), url = UUID.randomUUID().toString),
    Link(Client("mut@gmail.com", F, LocalDate.parse("1993-06-24")), url = UUID.randomUUID().toString)
  )

  val linkProducer = ProducerSettings(system, clientKeySerde.serializer(), linkSerde.serializer())

  Source
    .fromIterator(() => links.toIterator)
    .map(l => Message(new ProducerRecord(conf.topics.links, ClientKey(l.client), l), NotUsed))
    .via(Producer.flexiFlow(linkProducer))
    .to(Sink.foreach(e => logger warn e.toString))
    .run()

  val pageviews = Random.shuffle(links).take(2).map { link =>
    (ClientKey(link.client), Click(link.url, Map.empty))
  }

  val pageviewProducer = ProducerSettings(system, clientKeySerde.serializer(), pageviewSerde.serializer())

  Source
    .fromIterator(() => pageviews.toIterator)
    .map(c => Message(new ProducerRecord(conf.topics.views, c._1, c._2), NotUsed))
    .via(Producer.flexiFlow(pageviewProducer))
    .runWith(Sink.ignore)

  val purchaseProducer = ProducerSettings(system, clientKeySerde.serializer(), purchaseSerde.serializer())

  val purchase = Purchase(UUID.randomUUID().toString, Vector(Item("abc", 20.0, "", ItemCategory$.Cloth)))

  Source.single(pageviews.head._1)
    .map(c => Message(new ProducerRecord(conf.topics.purchases, c, purchase), NotUsed))
    .via(Producer.flexiFlow(purchaseProducer))
    .runWith(Sink.ignore)

  sys.addShutdownHook {
    logger error "Closing materializer and actor system."
    materializer.shutdown()
    system.terminate()
  }
}
