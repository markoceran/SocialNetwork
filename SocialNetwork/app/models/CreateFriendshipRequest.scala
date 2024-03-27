package models

import play.api.libs.json.{Format, Json}

case class CreateFriendshipRequest(forUserId: BigInt)

object CreateFriendshipRequest {
  implicit val createFriendshipRequestFormat: Format[CreateFriendshipRequest] = Json.format[CreateFriendshipRequest]
}
