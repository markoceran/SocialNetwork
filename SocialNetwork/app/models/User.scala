package models

import models.Gender.Gender
import play.api.libs.json._
import java.time.LocalDate

case class User(name: String, lastName: String, username: String, email: String, password: String, dateOfBirth: LocalDate, phoneNumber: String, gender: Gender)

object User {
  implicit val userFormat: Format[User] = Json.format[User]
}
