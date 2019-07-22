package fr.xebia.ldi.crocodile.stream.processor

import java.time.{LocalDate, LocalDateTime}

import com.madewithtea.mockedstreams.MockedStreams
import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Gender.F
import fr.xebia.ldi.crocodile.common.model.ItemCategory.Cloth
import fr.xebia.ldi.crocodile.common.model.{Coupon, Item, Purchase, Voucher}
import fr.xebia.ldi.crocodile.stream.CrocoSpec
import fr.xebia.ldi.crocodile.stream.model.{ActiveLink, UsedLink}
import fr.xebia.ldi.crocodile.stream.operator.Processors
import fr.xebia.ldi.crocodile.stream.processor.GrantCouponSpec.addHeaders
import org.apache.kafka.common.header.internals.{RecordHeader, RecordHeaders}
import org.apache.kafka.common.header.{Header, Headers}
import org.apache.kafka.streams.kstream.{ValueTransformer, ValueTransformerSupplier}
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.scala.ByteArrayKeyValueStore
import org.apache.kafka.streams.scala.kstream.Materialized
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

/**
  * Created by loicmdivad.
  */
class GrantCouponSpec extends FlatSpec with Matchers with GivenWhenThen with CrocoSpec {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes.String

  "grantCoupon" should "output a voucher to a client after a matching purchase" in {
    Given("a purchase matching a marketing campaign")
    val inputLink = ActiveLink("url", "bob@xebia.fr", F, LocalDate.of(1993, 6, 24), LocalDateTime.now())

    val inputPurchase = Purchase("id", Vector(Item("#1", 10.0, "White polo", Cloth)))

    val inputs = Vector((ClientKey("bob@xebia.fr"), UsedLink(inputLink, inputPurchase)))

    And("the some of coupons")
    And("and a stream topology including processor GrantCoupon")
    val topology = MockedStreams().topology { builder =>

      builder.globalTable("coupons", Materialized.as[String, Coupon, ByteArrayKeyValueStore]("coupons-store"))

      builder
        .stream[ClientKey, UsedLink]("input-topic")
        .transform(Processors.grantCoupon("coupons-store"))
        .to("output-topic")
    }

    When("the purchase events come in the flow")
    val flow = topology
      .stores("coupons-store" :: Nil)
      .input("coupons", String, CouponSerde, GrantCouponSpec.Coupons)
      .input("input-topic", ClientKeySerde, UsedLinkSerde, inputs)

    Then("a voucher is sent to the output stream")
    val outputResult = flow.output("output-topic", ClientKeySerde, VoucherSerde, 1)
    val stateResult = flow.stateTable("coupons-store")

    outputResult should have length 1
    outputResult.head._1 shouldBe ClientKey("bob@xebia.fr")
    outputResult.head._2 shouldBe Voucher()
  }

  it should "almost work" in {
    val inputLink = ActiveLink("url", "bob@xebia.fr", F, LocalDate.of(1993, 6, 24), LocalDateTime.now())

    val inputPurchase = Purchase("id", Vector(Item("#1", 10.0, "White polo", Cloth)))

    val inputs = Vector((ClientKey("bob@xebia.fr"), UsedLink(inputLink, inputPurchase)))

    When("")
    val topology = MockedStreams().topology { builder =>

      builder.globalTable("coupons", Materialized.as[String, Coupon, ByteArrayKeyValueStore]("coupons-store"))

      builder
        .stream[ClientKey, UsedLink]("input-topic")
        .transform(Processors.grantCoupon("coupons-store"))
        .to("output-topic")
    }

    val flow = topology
      .stores("coupons-store" :: Nil)
      .input("input-topic", ClientKeySerde, UsedLinkSerde, inputs)

    Then("")
    val outputResult = flow.output("output-topic", ClientKeySerde, VoucherSerde, 1)
    val stateResult = flow.stateTable("coupons-store")

    outputResult should have length 0
  }

  it should "skip purchase event with technical error headers" in {
    Given("a purchase matching the marketing campaign terms")
    val inputLink = ActiveLink("url", "bob@xebia.fr", F, LocalDate.of(1993, 6, 24), LocalDateTime.now())

    val inputPurchase = Purchase("id", Vector(Item("#1", 10.0, "White polo", Cloth)))

    val inputs = Vector((ClientKey("bob@xebia.fr"), UsedLink(inputLink, inputPurchase)))

    And("headers indicating a technical error")
    val headers: Headers = new RecordHeaders(
      Array[Header](
        new RecordHeader("invalid", "Error code: 111".getBytes),
        new RecordHeader("error-message", "Message invalid due to ...".getBytes)
      )
    )

    val topology = MockedStreams().topology { builder =>

      builder.globalTable("coupons", Materialized.as[String, Coupon, ByteArrayKeyValueStore]("coupons-store"))

      builder
        .stream[ClientKey, UsedLink]("input-topic")
        .transformValues(addHeaders[UsedLink](headers))
        .transform(Processors.grantCoupon("coupons-store"))
        .to("output-topic")
    }

    When("the purchase events come in the flow")
    val flow = topology
      .stores("coupons-store" :: Nil)
      .input("input-topic", ClientKeySerde, UsedLinkSerde, inputs)

    Then("no voucher is sent to the output stream")
    val result = flow.output("output-topic", ClientKeySerde, VoucherSerde, 1)
    val stateResult = flow.stateTable("coupons-store")

    result should have length 0
  }

}

object GrantCouponSpec {

  val Coupons = Coupon("#1") :: Coupon("#2") :: Coupon("#3") :: Nil map(c => (c.id, c)) toVector

  def addHeaders[T](headers: Headers): ValueTransformerSupplier[T, T] = () => new ValueTransformer[T, T] {

    var context: ProcessorContext = _

    override def init(context: ProcessorContext): Unit = this.context = context

    override def transform(value: T): T = {
      headers.forEach((h: Header) => context.headers().add(h))
      value
    }

    override def close(): Unit = ()
  }
}