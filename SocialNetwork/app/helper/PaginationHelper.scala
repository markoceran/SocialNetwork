package helper

object PaginationHelper {
  def validatePagination(pageNumber: Int, pageSize: Int): Boolean = {
      pageNumber > 0 && pageSize > 0 && pageSize <= 100
  }
}

