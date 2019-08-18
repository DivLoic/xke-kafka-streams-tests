package fr.xebia.ldi.crocodiles.datagen.config

import CrocoConf.CroroTopics

/**
  * Created by loicmdivad.
  */
case class CrocoConf(topics: CroroTopics)

object CrocoConf {

  case class CroroTopics(links: String, views: String, purchases: String)
}