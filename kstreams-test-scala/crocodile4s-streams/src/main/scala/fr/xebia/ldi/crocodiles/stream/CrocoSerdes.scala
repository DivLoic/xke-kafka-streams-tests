package fr.xebia.ldi.crocodiles.stream

import fr.xebia.ldi.crocodiles.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model._
import fr.xebia.ldi.crocodiles.common.CommonSerdes
import fr.xebia.ldi.crocodiles.common.model.{Click, Coupon, Link, Purchase, Voucher}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serde

/**
  * Created by loicmdivad.
  */
trait CrocoSerdes extends CommonSerdes {

  implicit def provideGenericAvro: () => GenericAvroSerde

  implicit lazy val LinkSerde: Serde[Link] = specificSerde[Link]
  implicit lazy val ClickSerde: Serde[Click] = specificSerde[Click]
  implicit lazy val PageviewSerde: Serde[Pageview] = specificSerde[Pageview]
  implicit lazy val PurchaseSerde: Serde[Purchase] = specificSerde[Purchase]
  implicit lazy val ActiveLinkSerde: Serde[ActiveLink] = specificSerde[ActiveLink]
  implicit lazy val CouponSerde: Serde[Coupon] = specificSerde[Coupon]
  implicit lazy val ClientKeySerde: Serde[ClientKey] = specificSerde[ClientKey]

  implicit lazy val UsedLinkSerde: Serde[UsedLink] = specificSerde[UsedLink]

  implicit lazy val VoucherSerde: Serde[Voucher] = specificSerde[Voucher]
}
