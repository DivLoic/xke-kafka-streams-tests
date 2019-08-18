package fr.xebia.ldi.crocodiles.common.model

/**
  * Created by loicmdivad.
  */
sealed trait CouponStatus

object CouponStatus {

  case object Used extends CouponStatus
  case object Available extends CouponStatus
}
