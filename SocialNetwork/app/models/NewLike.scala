package models

import play.api.libs.json._

case class NewLike(postId: BigInt)

object NewLike {
  implicit val newLikeFormat: Format[NewLike] = Json.format[NewLike]
}
