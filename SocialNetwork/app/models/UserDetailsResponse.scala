package models

import play.api.libs.json.{Format, Json}

case class UserDetailsResponse(id: BigInt, username: String)

object UserDetailsResponse {
  implicit val userDetailsResponseFormat: Format[UserDetailsResponse] = Json.format[UserDetailsResponse]
}
