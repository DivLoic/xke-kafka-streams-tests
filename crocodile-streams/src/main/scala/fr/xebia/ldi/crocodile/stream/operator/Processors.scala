package fr.xebia.ldi.crocodile.stream.operator

import java.lang

import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Voucher
import fr.xebia.ldi.crocodile.stream.model.UsedLink
import org.apache.kafka.common.header.Header
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.{Transformer, TransformerSupplier, ValueTransformer, ValueTransformerSupplier}
import org.apache.kafka.streams.processor.ProcessorContext

import scala.collection.JavaConverters._
/**
  * Created by loicmdivad.
  */
object Processors {

  val GrantCouponProcessor: TransformerSupplier[ClientKey, UsedLink, KeyValue[ClientKey, Voucher]] = () => new Transformer[ClientKey, UsedLink, KeyValue[ClientKey, Voucher]] {

    var context: ProcessorContext = _

    override def init(context: ProcessorContext): Unit = {
      this.context = context
    }

    def transform(key:ClientKey, value:UsedLink): KeyValue[ClientKey, Voucher] = {
      val validaitonFlagOpt = this.context.headers.headers("invalid").asScala.headOption

      println(validaitonFlagOpt)
      if(validaitonFlagOpt.isEmpty) new KeyValue(key, Voucher()) else null
    }

    override def close(): Unit = ()
  }
}
