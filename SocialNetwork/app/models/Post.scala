package models

import play.api.libs.json._

case class Post(id: BigInt, content: String, postedBy: UserDetailsResponse, edited: Boolean)

object Post {
  implicit val postFormat: Format[Post] = Json.format[Post]
}

