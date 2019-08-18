package fr.xebia.ldi.crocodiles.stream.mapper

import com.madewithtea.mockedstreams.MockedStreams
import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Device.Phone
import fr.xebia.ldi.crocodile.stream.model.Pageview
import fr.xebia.ldi.crocodile.stream.CrocoSpec
import fr.xebia.ldi.crocodiles.common.model.Click
import fr.xebia.ldi.crocodiles.stream.CrocoSpec
import fr.xebia.ldi.crocodiles.stream.model.Pageview
import fr.xebia.ldi.crocodiles.stream.operator.Mappers
import org.apache.kafka.common.utils.Bytes
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
/**
  * Created by loicmdivad.
  */
class ParseHeadersSpec extends FlatSpec with Matchers with GivenWhenThen with CrocoSpec {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes.Bytes
  import ParseHeadersSpec._

  "parseHeaders" should "extract information from the raw page view" in {
    Given("a series of url and http headers")
    val clicks = Seq(
      (null, Click(TestUrlExample, TestHerders + ("login" -> "alice@xebia.fr"))),
      (null, Click(TestUrlExample, TestHerders + ("login" -> "bob@xebia.fr"))),
      (null, Click(TestUrlExample, TestHerders + ("login" -> "elon@xebia.fr")))
    )

    And("a topology including the mapper parseHeaders")
    val topology = MockedStreams().topology { builder =>
      builder
        .stream[Bytes, Click]("input-topic")
        .map[ClientKey, Pageview](Mappers.parseHeaders)
        .to("output-topic")
    }

    When("the clicks come to the flow")
    val flow = topology.input("input-topic", Bytes, ClickSerde, clicks)

    Then("all elements are validated and transformed")
    val result = flow.output("output-topic", ClientKeySerde, PageviewSerde, 3)

    result should have length 3

    result.head._1.id shouldBe "alice@xebia.fr"
    result.head._2.id shouldBe "1b94deed1260"
    result.head._2.device shouldBe Phone

    result(1)._1.id shouldBe "bob@xebia.fr"
    result(1)._2.id shouldBe "1b94deed1260"
    result(1)._2.device shouldBe Phone

    result(2)._1.id shouldBe "elon@xebia.fr"
    result(2)._2.id shouldBe "1b94deed1260"
    result(2)._2.device shouldBe Phone
  }

  it should "invalidate events without login information" in {
    Given("a click event with url and http headers containing no login key")
    val clicks = Seq((null, Click(TestUrlExample, TestHerders - "login")))

    And("a topology including the mapper parseHeaders")
    val topology = MockedStreams().topology { builder =>
      builder
        .stream[Bytes, Click]("input-topic")
        .map[ClientKey, Pageview](Mappers.parseHeaders)
        .filterNot((k, _) => k == null)
        .to("output-topic")
    }

    When("the clicks come to the flow")
    val flow = topology.input("input-topic", Bytes, ClickSerde, clicks)

    Then("the element is invalid and filter from the stream")
    val result = flow.output("output-topic", ClientKeySerde, PageviewSerde, 1)

    result should have length 0
  }

  // TODO: add other edges cases

}

object ParseHeadersSpec {

  val TestUrlExample = "http://xebi-crocodile.io/v1.0/4b24d1ef-d615-43f7-9a6c-1b94deed1260"

  val TestHerders: Map[String, String] = Map(
    "login" -> "ldivad@xebia.fr",
    "device" -> "croco:device(phone)@iphoneXS",
    "dt" -> "2018-12-07T21:42:11+01:00[Europe/Paris]"
  )
}