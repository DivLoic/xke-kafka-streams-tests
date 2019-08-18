package fr.xebia.ldi.crocodiles.stream

import java.util.Properties

import com.madewithtea.mockedstreams.MockedStreams
import fr.xebia.ldi.crocodiles.stream.operator.Mappers
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.Serdes
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

/**
  * Created by loicmdivad.
  */
class MockedStreamExampleSpec extends FlatSpec with Matchers with GivenWhenThen with CrocoConversion {

  import Serdes.{Integer, String}
  import org.apache.kafka.streams.scala.ImplicitConversions._

  val props: Properties = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "topology-driver-example")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "uselesshost:0000")

  "getPrice" should "map a JSON payload to an integer price" in {

    Given("a correct JSON input")
    val input = Seq(("12345", """{"item":"#1","price":90.99}"""))

    And("the value mapper getPrice")
    val topology = MockedStreams().topology { builder =>
      builder
        .stream[String, String]("input-topic")
        .mapValues(Mappers.getPrice)
        .to("output-topic")
    }

    When("the input comes in the flow")
    val flow = topology.input("input-topic", String, String, input)

    Then("an integer value is outputted")
    val result = flow.output("output-topic", String, Integer, 1)

    result shouldEqual Seq(("12345", 9099))
  }

  it should "return -1 for any extraction problem" in {
    Given("a incorrect JSON inputs")
    val input1 = Seq(
      ("101", """{"item":"#1"}"""),
      ("102", """{"item":"#1","price":"a lot of money"}"""),
      ("103", """NOT EVEN A JSON ... ¯\_(ツ)_/¯""")
    )

    And("the value mapper getPrice")
    val topology = MockedStreams().topology { builder =>
      builder
        .stream[String, String]("input-topic")
        .mapValues(Mappers.getPrice)
        .to("output-topic")
    }

    When("the inputs come in the flow")
    val flow = topology.input("input-topic", String, String, input1)

    Then("the price is replaced by -1")
    val result = flow.output("output-topic", String, Integer, 3)

    result should contain allElementsOf Seq(("101", -1), ("102", -1), ("103", -1))
  }
}
