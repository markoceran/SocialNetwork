package models

import play.api.libs.json._

case class User(id: BigInt, username: String, password: String)

object User {
  implicit val userFormat: Format[User] = Json.format[User]
}
