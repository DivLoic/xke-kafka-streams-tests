package fr.xebia.ldi.crocodile.stream

import java.time.LocalDate

import com.madewithtea.mockedstreams.MockedStreams
import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Gender.M
import fr.xebia.ldi.crocodile.common.model._
import fr.xebia.ldi.crocodile.stream.config.CrocoConf
import fr.xebia.ldi.crocodile.stream.config.CrocoConf.{CrocoTopics, CrocoStores}
import org.apache.kafka.streams.scala.Serdes
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

import scala.concurrent.duration._

class CrocoProjectSpec extends FlatSpec with Matchers with GivenWhenThen with MainTopology with CrocoSpec {

  import CrocoProjectSpec._

  "MainTopology" should "works" in {
    Given("all the input events from ")
    val inputKey = ClientKey(InputClient.email)
    val inputLink = Link(client = InputClient, url = "http://xebi-crocodile.io/v1.0/4b24d1ef-d615-43f7-9a6c-1b94deed1260")
    val inputClick = Click(inputLink.url, InputHerders)
    val inputPurchase = Purchase(cardId = "#1", items = Vector.empty)

    And("the complete topology")
    val config = CrocoConf(
      app = "my-awesome-app-name🐊🐊🐊",
      bootstrapServer = "notused:0000",
      schemaRegistryUrl = "notused:0000",
      correlationWindow = 30 seconds,
      stores = CrocoStores("coupons"),
      topics = CrocoTopics("links", "clicks", "purchases", "coupons", "vouchers")
    )

    val topology = MockedStreams().topology(buildTopology(config))

    When("a user receive a link")
    And("he clicks on the link")
    And("then buy a item in-store")
    val flow = topology
      .input("links", ClientKeySerde, LinkSerde, Seq((inputKey, inputLink)))
      .input("clicks", Serdes.Bytes, ClickSerde, Seq((null, inputClick)))
      .input("purchases", ClientKeySerde, PurchaseSerde, Seq((inputKey, inputPurchase)))

    Then("a coupon is sent to the user")
    val result = flow.output("vouchers", ClientKeySerde, VoucherSerde, 1)

    result should have length 1
  }
}

object CrocoProjectSpec {

  val InputClient = Client("client@baldibla.com", M, LocalDate.of(1993, 2, 7))

  val InputHerders: Map[String, String] = Map(
    "login" -> InputClient.email,
    "device" -> "croco:device(phone)@iphone6S+",
    "dt" -> "2018-06-24T12:42:11+01:00[Europe/Paris]"
  )
}
