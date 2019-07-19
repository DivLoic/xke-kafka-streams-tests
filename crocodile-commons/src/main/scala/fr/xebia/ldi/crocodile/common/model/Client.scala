package fr.xebia.ldi.crocodile.common.model

import java.time.LocalDate

/**
  * Created by loicmdivad.
  */
case class Client(email: String, gender: Gender, birthDate: LocalDate)

object Client {

  case class ClientKey(id: String)

  object ClientKey {

    def apply(client: Client): ClientKey = new ClientKey(client.email)
  }
}