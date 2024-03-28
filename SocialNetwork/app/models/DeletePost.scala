package models

import play.api.libs.json.{Format, Json}

case class DeletePost(postId: BigInt)

object DeletePost {
  implicit val deletePostFormat: Format[DeletePost] = Json.format[DeletePost]
}
