package models

import play.api.libs.json._

import java.time.LocalDateTime

case class Post(id: BigInt, content: String, postedBy: UserDetailsResponse, creationDate: LocalDateTime, edited: Boolean)

object Post {
  implicit val postFormat: Format[Post] = Json.format[Post]
}

