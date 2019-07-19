package fr.xebia.ldi.crocodile.common.model

/**
  * Created by loicmdivad.
  */
sealed trait ItemCategory

object ItemCategory extends Enumeration with ItemCategory {
  val Shoes, Cloth, Accessory = Value
}


