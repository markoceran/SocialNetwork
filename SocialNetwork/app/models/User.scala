package models

import play.api.libs.json._

case class User(name: String, lastName: String, username: String, email: String, password: String)

object User {
  implicit val userFormat: Format[User] = Json.format[User]
}
