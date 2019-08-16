package fr.xebia.ldi.crocodile.stream.config

import CrocoConf.{CrocoStores, CrocoTopics}

import scala.concurrent.duration.FiniteDuration


/**
  * Created by loicmdivad.
  */
case class CrocoConf(app: String,
                     topics: CrocoTopics,
                     stores: CrocoStores,
                     bootstrapServer: String,
                     schemaRegistryUrl: String,
                     correlationWindow: FiniteDuration)

object CrocoConf {

  case class CrocoStores(coupons: String)

  case class CrocoTopics(links: String, clicks: String, purchases: String, coupons: String, vouchers: String)
}