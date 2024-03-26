package models

import play.api.libs.json.{Format, Json}

case class FriendshipRequest(id: BigInt, forUser: UserDetailsResponse, fromUser: UserDetailsResponse, approved: Boolean)

object FriendshipRequest {
  implicit val friendshipRequestFormat: Format[FriendshipRequest] = Json.format[FriendshipRequest]
}
