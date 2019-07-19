package fr.xebia.ldi.crocodile.common.model

import fr.xebia.ldi.crocodile.common.model.CouponStatus.Available


/**
  * Created by loicmdivad.
  */
case class Coupon(id: String, status: CouponStatus = Available)