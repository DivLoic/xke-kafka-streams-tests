package fr.xebia.ldi.crocodile.common

import java.time.Duration

import scala.concurrent.duration.FiniteDuration

/**
  * Created by loicmdivad.
  */
trait CrocoTools {

  implicit class ScalaDuration(value: FiniteDuration) {
    def asJava: Duration = Duration.ofNanos(value.toNanos)
  }
}
