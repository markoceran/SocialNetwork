package models

import play.api.libs.json._

case class Like(id: BigInt, userId: BigInt, postId: BigInt)

object Like {
  implicit val likeFormat: Format[Like] = Json.format[Like]
}
