package models

import play.api.libs.json.{Format, Json}

case class NewUsernameRequest(newUsername: String)

object NewUsernameRequest {
  implicit val newUsernameRequestFormat: Format[NewUsernameRequest] = Json.format[NewUsernameRequest]
}


