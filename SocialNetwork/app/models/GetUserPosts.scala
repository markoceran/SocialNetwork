package models

import play.api.libs.json.{Format, Json}

case class GetUserPosts(username: String, pagination: Pagination)

object GetUserPosts {
  implicit val getUserPostsFormat: Format[GetUserPosts] = Json.format[GetUserPosts]
}
