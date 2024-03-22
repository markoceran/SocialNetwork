package models

import play.api.libs.json.{Format, Json}

case class LoginRequest(username: String, password: String)

object LoginRequest {
  implicit val loginRequestFormat: Format[LoginRequest] = Json.format[LoginRequest]
}