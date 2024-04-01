package models

import play.api.libs.json.{Format, Json}

case class Friendship(id: BigInt, user_id: BigInt, friend_id: BigInt)

object Friendship {
  implicit val friendshipFormat: Format[Friendship] = Json.format[Friendship]
}

