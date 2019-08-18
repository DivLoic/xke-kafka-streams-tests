package fr.xebia.ldi.crocodiles.stream.operator

import fr.xebia.ldi.crocodile.common.model.Purchase
import fr.xebia.ldi.crocodile.stream.model.{ActiveLink, UsedLink}
import fr.xebia.ldi.crocodiles.common.model.{Link, Purchase}
import fr.xebia.ldi.crocodiles.stream.model.{ActiveLink, Pageview, UsedLink}
import org.apache.kafka.streams.kstream.ValueJoiner

/**
  * Created by loicmdivad.
  */
object Joiners {

  val PageviewLinkJoiner: ValueJoiner[Pageview, Link, ActiveLink] = (view: Pageview, link: Link) => ActiveLink(view.id,
    link.client.email,
    link.client.gender,
    link.client.birthDate,
    view.datetime
  )

  val ActiveLinkPurchaseJoiner: ValueJoiner[ActiveLink, Purchase, UsedLink] = UsedLink.apply
}
