package fr.xebia.ldi.crocodile.stream.model

import java.time.LocalDateTime

import fr.xebia.ldi.crocodile.common.model.Device

/**
  * Created by loicmdivad.
  */
case class Pageview(id: String, device: Device, datetime: LocalDateTime)

object Pageview