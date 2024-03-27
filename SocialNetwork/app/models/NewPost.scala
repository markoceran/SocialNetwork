package models

import play.api.libs.json.{Format, Json}

case class NewPost(content: String)

object NewPost {
  implicit val newPostFormat: Format[NewPost] = Json.format[NewPost]
}