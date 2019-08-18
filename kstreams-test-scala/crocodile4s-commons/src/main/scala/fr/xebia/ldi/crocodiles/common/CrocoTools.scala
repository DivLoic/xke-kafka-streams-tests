package fr.xebia.ldi.crocodiles.common

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
