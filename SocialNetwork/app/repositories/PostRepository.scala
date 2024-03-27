package repositories

import anorm.SQL
import play.api.db.Database
import repositories.DatabaseExecutionContext.databaseExecutionContext

import javax.inject.Inject
import scala.concurrent.Future

class PostRepository @Inject()(db: Database){

  def createPost(postedById: BigInt, content: String): Future[Boolean] = Future {
    val ID: Option[Long] = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO post (content, postedByUser_id, edited)
        VALUES ({content}, {postedByUser_id}, {edited})
        """
      ).on(
        "content" -> content,
        "postedByUser_id" -> postedById,
        "edited" -> false,
      ).executeInsert()
    }
    ID.isDefined
  }(databaseExecutionContext)


}
