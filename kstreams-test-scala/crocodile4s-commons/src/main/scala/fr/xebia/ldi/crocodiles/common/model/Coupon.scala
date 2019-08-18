package fr.xebia.ldi.crocodiles.common.model

import CouponStatus.Available


/**
  * Created by loicmdivad.
  */
case class Coupon(id: String, status: CouponStatus = Available)