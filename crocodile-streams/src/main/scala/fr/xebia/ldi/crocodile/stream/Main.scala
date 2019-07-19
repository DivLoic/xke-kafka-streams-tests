package fr.xebia.ldi.crocodile.stream

import java.util.Properties

import fr.xebia.ldi.crocodile.common.CrocoTools
import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.{Click, Coupon, Link, Purchase}
import fr.xebia.ldi.crocodile.stream.config.CrocoConf
import fr.xebia.ldi.crocodile.stream.operator.{Joiners, Mappers, Predicates, Processors}
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.{GlobalKTable, JoinWindows}
import org.apache.kafka.streams.scala.kstream.{KStream, KTable, Materialized}
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import org.slf4j.LoggerFactory
import pureconfig.generic.auto._

import scala.collection.JavaConverters._


/**
  * Created by loicmdivad.
  */
trait MainTopology extends CrocoSerdes with CrocoConversion with CrocoTools {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes.{Bytes, String}

  def configure(url: String, isKey: Boolean)(serde: Serde[_]): Unit = serde
    .configure(Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> url).asJava, isKey)

  def buildTopology(config: CrocoConf)(builder: StreamsBuilder = new StreamsBuilder): Topology = {

    val links: KTable[ClientKey, Link] = builder.table(config.topics.links)

    val clicks: KStream[Bytes, Click] = builder.stream(config.topics.clicks)

    val purchases: KStream[ClientKey, Purchase] = builder.stream(config.topics.purchases)

    val materialized = Materialized.as[String, Coupon, ByteArrayKeyValueStore](config.stores.coupons)

    val coupons: GlobalKTable[String, Coupon] = builder.globalTable(config.topics.coupons, materialized)

    clicks

      .map(Mappers.parseHeaders)

      .filterNot((k, _) => k == null)

      .filter(Predicates.isMobileDevice)

      .join(links)(Joiners.PageviewLinkJoiner)

      .join(purchases)(Joiners.ActiveLinkPurchaseJoiner, JoinWindows.of(config.correlationWindow.asJava))

      .transform(Processors.GrantCouponProcessor)

      .to(config.topics.vouchers)

    builder.build()
  }
}

object MainApplication extends MainTopology with App {

  Welcome.hello

  val logger = LoggerFactory.getLogger(getClass)

  implicit def provideGenericAvro: () => GenericAvroSerde = () => new GenericAvroSerde

  pureconfig.loadConfig[CrocoConf]("crocodile").map { config =>

    configure(config.schemaRegistryUrl, isKey = true)(ClientKeySerde)

    LinkSerde :: PurchaseSerde :: PageviewSerde :: ActiveLinkSerde :: VoucherSerde :: UsedLinkSerde ::
      Nil foreach configure(config.schemaRegistryUrl, isKey = false)

    val properties = new Properties

    properties.putAll(Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> config.app,
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> config.bootstrapServer,
      StreamsConfig.PROCESSING_GUARANTEE_CONFIG -> StreamsConfig.EXACTLY_ONCE).asJava
    )

    val topology = MainApplication.buildTopology(config)()

    logger info topology.describe().toString

    val streams = new KafkaStreams(topology, properties)

    sys.addShutdownHook {
      streams.close()
    }

    streams.cleanUp()

    streams.start()

  }.left.map { failures =>

    logger error "Failed to parse the configuration."

    failures.toList.foreach(err => logger.error(err.description))

    sys.exit(-1)
  }
}
