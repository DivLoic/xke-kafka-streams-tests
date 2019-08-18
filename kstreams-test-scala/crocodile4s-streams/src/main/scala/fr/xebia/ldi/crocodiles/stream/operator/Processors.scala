package fr.xebia.ldi.crocodiles.stream.operator

import fr.xebia.ldi.crocodiles.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Coupon
import fr.xebia.ldi.crocodile.stream.model.UsedLink
import fr.xebia.ldi.crocodiles.common.model.{Coupon, Voucher}
import fr.xebia.ldi.crocodiles.stream.model.UsedLink
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.{Transformer, TransformerSupplier}
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
object Processors {

  def grantCoupon(couponsStoreName: String): TransformerSupplier[ClientKey, UsedLink, KeyValue[ClientKey, Voucher]] =

    () => new Transformer[ClientKey, UsedLink, KeyValue[ClientKey, Voucher]] {

      var context: ProcessorContext = _
      var couponsStore: KeyValueStore[String, Coupon] = _

      override def init(context: ProcessorContext): Unit = {
        this.context = context
        this.couponsStore = this.context.getStateStore(couponsStoreName).asInstanceOf[KeyValueStore[String, Coupon]]
      }

      def transform(key: ClientKey, value: UsedLink): KeyValue[ClientKey, Voucher] = {
        val validaitonFlagOpt = this.context.headers.headers("invalid").asScala.headOption

        println(validaitonFlagOpt)
        if (validaitonFlagOpt.isEmpty) new KeyValue(key, Voucher()) else null
      }

      override def close(): Unit = ()
    }
}
