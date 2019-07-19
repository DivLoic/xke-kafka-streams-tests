package fr.xebia.ldi.crocodile.stream.operator

import fr.xebia.ldi.crocodile.common.model.Client.ClientKey
import fr.xebia.ldi.crocodile.common.model.Device.Mobile
import fr.xebia.ldi.crocodile.stream.model.Pageview
import org.apache.kafka.streams.kstream.Predicate

/**
  * Created by loicmdivad.
  */
object Predicates {

  val isMobileDevice: Predicate[ClientKey, Pageview] = (_, view) => view.device.isInstanceOf[Mobile]
}
