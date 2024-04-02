package models

import play.api.libs.json._

import java.time.LocalDateTime

case class PostDetailsResponse(id: BigInt, content: String, postedBy: UserDetailsResponse, creationDate: LocalDateTime, edited: Boolean, likeCount: Int, liked: Boolean)

object PostDetailsResponse {
  implicit val postDetailsResponseFormat: Format[PostDetailsResponse] = Json.format[PostDetailsResponse]
}

