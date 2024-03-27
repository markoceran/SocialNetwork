package models

import play.api.libs.json.{Format, Json}

case class AcceptDenyRequest(requestId: BigInt)

object AcceptDenyRequest {
  implicit val requestFormat: Format[AcceptDenyRequest] = Json.format[AcceptDenyRequest]
}
