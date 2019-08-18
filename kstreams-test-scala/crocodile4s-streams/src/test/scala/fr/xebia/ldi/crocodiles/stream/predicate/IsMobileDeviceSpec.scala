package fr.xebia.ldi.crocodiles.stream.predicate

import java.time.LocalDateTime

import com.madewithtea.mockedstreams.MockedStreams
import fr.xebia.ldi.crocodiles.common.model.Client.ClientKey
import fr.xebia.ldi.crocodiles.common.model.Device.{Computer, Laptop, Phone}
import fr.xebia.ldi.crocodile.stream.CrocoSpec
import fr.xebia.ldi.crocodile.stream.model.Pageview
import fr.xebia.ldi.crocodile.stream.operator.Predicates
import fr.xebia.ldi.crocodiles.stream.CrocoSpec
import fr.xebia.ldi.crocodiles.stream.model.Pageview
import fr.xebia.ldi.crocodiles.stream.operator.Predicates
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

/**
  * Created by loicmdivad.
  */
class IsMobileDeviceSpec extends FlatSpec with Matchers with GivenWhenThen with CrocoSpec {

  import org.apache.kafka.streams.scala.ImplicitConversions._

  "isMobileDevice" should "keep only the clicks from mobile device" in {
    Given("a series of clicks")
    val clicks = Seq(
      (ClientKey("#1"), Pageview("url-a", Computer, LocalDateTime.now())),
      (ClientKey("#2"), Pageview("url-b", Phone, LocalDateTime.now())),
      (ClientKey("#3"), Pageview("url-c", Laptop, LocalDateTime.now()))
    )

    And("a topology including the filter isMobileDevice")
    val topology = MockedStreams().topology { builder =>
      builder
        .stream[ClientKey, Pageview]("input-topic")
        .filter(Predicates.isMobileDevice)
        .to("output-topic")
    }

    When("the clicks come to the flow")
    val flow = topology.input("input-topic", ClientKeySerde, PageviewSerde, clicks)

    Then("only the clicks coming from mobile device remains")
    val result = flow.output("output-topic", ClientKeySerde, PageviewSerde, 3)

    result should have length 1
    result.head._1.id shouldBe "#2"
    result.head._2.id shouldBe "url-b"
  }
}
