package models

import play.api.libs.json.{Format, Json}

case class UpdatePost(postId: BigInt, newContent: String)

object UpdatePost {
  implicit val updatePostFormat: Format[UpdatePost] = Json.format[UpdatePost]
}