package fr.xebia.ldi.crocodiles.stream.model

import java.time.LocalDateTime

import fr.xebia.ldi.crocodiles.common.model.Device

/**
  * Created by loicmdivad.
  */
case class Pageview(id: String, device: Device, datetime: LocalDateTime)

object Pageview