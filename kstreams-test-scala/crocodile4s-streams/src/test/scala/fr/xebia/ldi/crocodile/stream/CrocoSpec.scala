package fr.xebia.ldi.crocodile.stream

import java.util.Properties

import fr.xebia.ldi.crocodile.common.{CommonSerdes, CrocoTools}
import fr.xebia.ldi.crocodile.stream.CrocoSpec.mockedRegistry
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.StreamsConfig

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
trait CrocoSpec extends CommonSerdes with CrocoSerdes with CrocoConversion with CrocoTools {

  val props: Properties = new Properties

  implicit def provideGenericAvro: () => GenericAvroSerde = () => new GenericAvroSerde(mockedRegistry)

  def configure(isKey: Boolean)(serde: Serde[_]): Unit = serde.configure(Map(
    AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS -> true,
    AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "notused:0000"
  ).asJava, isKey)

  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "topology-driver-example")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "notused:0000")

  LinkSerde ::
    ClickSerde ::
    CouponSerde ::
    VoucherSerde ::
    PageviewSerde ::
    PurchaseSerde ::
    UsedLinkSerde ::
    ActiveLinkSerde :: Nil foreach configure(isKey = false)

  configure(isKey = true)(ClientKeySerde)
}

object CrocoSpec {

  val mockedRegistry = new MockSchemaRegistryClient
}