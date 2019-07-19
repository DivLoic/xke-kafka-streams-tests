package fr.xebia.ldi.crocodile.stream

import java.util.Properties

import fr.xebia.ldi.crocodile.stream.operator.Mappers
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.test.{ConsumerRecordFactory, OutputVerifier}
import org.apache.kafka.streams.{StreamsConfig, TopologyTestDriver}
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

/**
  * Created by loicmdivad.
  */
class TopologyDriverExampleSpec extends FlatSpec with Matchers with GivenWhenThen with CrocoConversion {

  import Serdes.{Integer, String}
  import org.apache.kafka.streams.scala.ImplicitConversions._

  val props: Properties = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "topology-driver-example")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "uselesshost:0000")

  private val recordFactory = new ConsumerRecordFactory[String, String](String.serializer, String.serializer)

  "getPrice" should "map a JSON payload to an integer price" in {
    val builder: StreamsBuilder = new StreamsBuilder

    Given("a correct JSON input")
    val input: ConsumerRecord[Array[Byte], Array[Byte]] =
      recordFactory.create("input-topic", "12345", """{"item":"#1","price":90.99}""")

    And("the value mapper getPice")
    builder
      .stream[String, String]("input-topic")
      .mapValues(Mappers.getPrice)
      .to("output-topic")

    val testDriver: TopologyTestDriver = new TopologyTestDriver(builder.build(), props)

    When("the input comes in the flow")
    testDriver.pipeInput(input)

    Then("a double value is outputted")
    val result: ProducerRecord[String, Int] = testDriver
      .readOutput("output-topic", String.deserializer, Integer.deserializer)

    OutputVerifier.compareKeyValue(result, "12345", 9099)
  }

  it should "return -1 for any extraction problem" in {
    val builder: StreamsBuilder = new StreamsBuilder

    Given("a incorrect JSON inputs")
    val inputs = Seq(
      recordFactory.create("input-topic", "101", """{"item":"#1"}"""),
      recordFactory.create("input-topic", "102", """{"item":"#1","price":"a lot of money"}"""),
      recordFactory.create("input-topic", "103", """NOT EVEN A JSON ... ¯\_(ツ)_/¯""")
    )

    And("the value mapper getPrice")
    builder
      .stream[String, String]("input-topic")
      .mapValues(Mappers.getPrice)
      .to("output-topic")

    val testDriver: TopologyTestDriver = new TopologyTestDriver(builder.build(), props)

    When("the inputs come in the flow")
    testDriver.pipeInput(inputs.head)
    testDriver.pipeInput(inputs(1))
    testDriver.pipeInput(inputs(2))

    Then("the price is replaced by -1")
    val result1: ProducerRecord[String, Int] = testDriver
      .readOutput("output-topic", String.deserializer, Integer.deserializer)

    val result2: ProducerRecord[String, Int] = testDriver
      .readOutput("output-topic", String.deserializer, Integer.deserializer)

    val result3: ProducerRecord[String, Int] = testDriver
      .readOutput("output-topic", String.deserializer, Integer.deserializer)

    OutputVerifier.compareKeyValue(result1, "101", -1)
    OutputVerifier.compareKeyValue(result2, "102", -1)
    OutputVerifier.compareKeyValue(result3, "103", -1)
  }
}
