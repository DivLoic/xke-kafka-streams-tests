package fr.xebia.ldi.crocodile.stream.model

import java.time.{LocalDate, LocalDateTime}

import fr.xebia.ldi.crocodile.common.model.Gender

/**
  * Created by loicmdivad.
  */
case class ActiveLink(url: String,
                      email: String,
                      gender: Gender,
                      birthDate: LocalDate,
                      datetime: LocalDateTime)
