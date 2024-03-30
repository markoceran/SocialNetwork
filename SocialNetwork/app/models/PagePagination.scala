package models

import play.api.libs.json.{Format, Json}

case class PagePagination(username: String,  pageNumber: Option[Int] = Some(1), pageSize: Option[Int] = Some(10))

object PagePagination {
  implicit val pagePaginationFormat: Format[PagePagination] = Json.format[PagePagination]
}

case class PagePagination2(pageNumber: Option[Int] = Some(1), pageSize: Option[Int] = Some(10))

object PagePagination2 {
  implicit val pagePagination2Format: Format[PagePagination2] = Json.format[PagePagination2]
}