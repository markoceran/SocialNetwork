package models

import play.api.libs.json._

case class LikeRequest(postId: BigInt)

object LikeRequest {
  implicit val likeRequestFormat: Format[LikeRequest] = Json.format[LikeRequest]
}
