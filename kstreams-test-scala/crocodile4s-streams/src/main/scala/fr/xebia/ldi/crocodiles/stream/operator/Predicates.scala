package fr.xebia.ldi.crocodiles.stream.operator

import fr.xebia.ldi.crocodiles.common.model.Client.ClientKey
import fr.xebia.ldi.crocodiles.common.model.Device.Mobile
import fr.xebia.ldi.crocodiles.stream.model.Pageview
import org.apache.kafka.streams.kstream.Predicate

/**
  * Created by loicmdivad.
  */
object Predicates {

  val isMobileDevice: Predicate[ClientKey, Pageview] = (_, view) => view.device.isInstanceOf[Mobile]
}
