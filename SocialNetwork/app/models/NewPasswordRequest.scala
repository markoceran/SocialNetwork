package models

import play.api.libs.json.{Format, Json}

case class NewPasswordRequest(newPassword: String, newPasswordAgain: String)

object NewPasswordRequest {
  implicit val newPasswordRequestFormat: Format[NewPasswordRequest] = Json.format[NewPasswordRequest]
}
