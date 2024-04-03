package models

import play.api.libs.json.{Format, Json}

case class UserSearch(prefix: String, pagination: Pagination)

object UserSearch {
  implicit val userSearchFormat: Format[UserSearch] = Json.format[UserSearch]
}
