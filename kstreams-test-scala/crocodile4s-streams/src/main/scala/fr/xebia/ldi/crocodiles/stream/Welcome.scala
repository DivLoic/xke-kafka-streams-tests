package fr.xebia.ldi.crocodiles.stream

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by loicmdivad.
  */
object Welcome {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  def hello = logger info """
      |   ___                        _ __ _          _
      |  / __\ __ ___   ___ ___   __| (🐊) | ___    /_\  _ __  _ __
      | / / | '__/ _ \ / __/ _ \ / _` |  | |/ _ \  //_\\| '_ \| '_ \
      |/ /🐊| | | (_) | (_| (_) | (_| |  | |  __/ /  _  \ |_) | |_) |
      |\____/_|  \___/ \___\___/ \__,_|__|_|\___| \_/ \_/ .__/| .__/🐊
      |                                                 |_|   |_|
    """.stripMargin
}
