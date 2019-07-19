package fr.xebia.ldi.crocodile.common.model

import scala.util.matching.Regex

/**
  * Created by loicmdivad.
  */
sealed trait Device {

  def httpHeaderName: String = getClass.getSimpleName.stripSuffix("$").toLowerCase
}

object Device {

  sealed trait Mobile

  val DevicePattern: Regex = """croco:device\((\w+)\)@(.*)""".r

  case object Phone extends Device with Mobile

  case object Tablet extends Device with Mobile

  case object Watch extends Device with Mobile

  case object Computer extends Device

  case object Laptop extends Device

  case object Raspberry extends Device

  def withName(tag: String): Option[Device] = tag match {
    case DevicePattern(term, _) if term == Phone.httpHeaderName => Some(Phone)
    case DevicePattern(term, _) if term == Tablet.httpHeaderName => Some(Tablet)
    case DevicePattern(term, _) if term == Watch.httpHeaderName => Some(Watch)
    case DevicePattern(term, _) if term == Phone.httpHeaderName => Some(Laptop)
    case _ => None
  }
}