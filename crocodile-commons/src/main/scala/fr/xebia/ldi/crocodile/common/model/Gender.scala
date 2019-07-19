package fr.xebia.ldi.crocodile.common.model

/**
  * Created by loicmdivad.
  */
sealed trait Gender

object Gender {

  case object M extends Gender

  case object F extends Gender

  case object Child extends Gender
}


