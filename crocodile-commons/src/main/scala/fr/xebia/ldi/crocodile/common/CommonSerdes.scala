package fr.xebia.ldi.crocodile.common

import java.util

import com.sksamuel.avro4s._
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

/**
  * Created by loicmdivad.
  */
trait CommonSerdes {

  def specificSerde[T >: Null : SchemaFor : Encoder : Decoder](implicit serde: () => GenericAvroSerde): Serde[T] =

    Serdes.serdeFrom(

      new Serializer[T] {

        val schema: Schema = AvroSchema[T]

        val inner: Serializer[GenericRecord] = serde().serializer()

        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
          inner.configure(configs, isKey)

        override def serialize(topic: String, data: T): Array[Byte] =
          inner.serialize(topic, Option(data).map(RecordFormat(schema).to).orNull)

        override def close(): Unit = inner.close()
      },

      new Deserializer[T] {

        val schema: Schema = AvroSchema[T]

        val inner: Deserializer[GenericRecord] = serde().deserializer()

        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
          inner.configure(configs, isKey)

        override def deserialize(topic: String, data: Array[Byte]): T =
          Option(data).map { nonNullByteArray =>
            RecordFormat(schema)
              .from(inner.deserialize(topic, nonNullByteArray))
          }.orNull

        override def close(): Unit = inner.close()
      }
    )
}
