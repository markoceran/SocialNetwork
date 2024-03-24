package models

import play.api.libs.json.{Format, Json}

case class NewUsernameRequest(username: String)

object NewUsernameRequest {
  implicit val newUsernameRequestFormat: Format[NewUsernameRequest] = Json.format[NewUsernameRequest]
}


