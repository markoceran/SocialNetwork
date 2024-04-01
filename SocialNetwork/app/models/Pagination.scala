package models

import play.api.libs.json.{Format, Json}

case class Pagination(pageNumber: Option[Int] = Some(1), pageSize: Option[Int] = Some(10))

object Pagination {
  implicit val paginationFormat: Format[Pagination] = Json.format[Pagination]
}