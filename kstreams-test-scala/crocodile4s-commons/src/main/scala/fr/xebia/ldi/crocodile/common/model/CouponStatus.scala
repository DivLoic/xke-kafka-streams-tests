package fr.xebia.ldi.crocodile.common.model

/**
  * Created by loicmdivad.
  */
sealed trait CouponStatus

object CouponStatus {

  case object Used extends CouponStatus
  case object Available extends CouponStatus
}
