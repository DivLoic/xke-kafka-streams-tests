package fr.xebia.ldi.crocodiles.common.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Click._

import scala.util.Try
import scala.util.matching.Regex

/**
  * Created by loicmdivad.
  */
case class Click(url: String, headers: Map[String, String]) {

  def deviceOption: Option[Device] = headers.get("device").flatMap(Device.withName)

  def loginOption: Option[String] = headers.get("login").flatMap(LoginPattern.findFirstIn)

  def codeOption: Option[String] = Try(UrlPattern.findAllIn(url).group(4)).toOption

  def visitTimeOption: Option[LocalDateTime] = headers.get("dt")
    .flatMap(dt => Try(LocalDateTime.parse(dt, DateTimePattern)).toOption)

  // examples ...

  def other: Nothing = ???

  def great: Nothing = ???

  def functions: Nothing = ???

  def definedByTheCoreTeam: Nothing = ???
}

object Click {

  val DateTimePattern: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  val LoginPattern: Regex = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".r

  val UrlPattern: Regex = "http://xebi-crocodile.io/(v\\d.\\d)/(\\w{8})([-\\w]{16})(\\w{12})".r
}