package fr.xebia.ldi.crocodile.stream.operator

import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model._
import fr.xebia.ldi.crocodile.stream.model
import fr.xebia.ldi.crocodile.stream.model.Pageview
import io.circe.parser.parse
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.{KeyValueMapper, ValueMapper}

import scala.util.Try

/**
  * Created by loicmdivad.
  */
object Mappers {

  val getPrice: ValueMapper[String, Int] = (value: String) =>

    parse(value).toOption.flatMap {
      _.hcursor
        .downField("price")
        .as[Double]
        .toOption
        .map(_ * 100 toInt)
    }.getOrElse(-1)

  val extractClient: ValueMapper[Link, Client] = (valueLink: Link) => valueLink.client

  val extractItems: ValueMapper[Purchase, Iterable[Item]] = (purchase: Purchase) => purchase.items

  val parseHeaders: KeyValueMapper[Bytes, Click, KeyValue[ClientKey, Pageview]] = (_, click) => (for {

      code <- click.codeOption

      login <- click.loginOption

      device <- click.deviceOption

      datetime <- click.visitTimeOption

      _ <- Try(assert(code.length equals 12)).toOption // and any other business logic rules you'd like

    } yield KeyValue.pair(ClientKey(login), model.Pageview(code, device, datetime))) orNull
}
