package repositories

import anorm.{Macro, RowParser, SQL, SqlParser, SqlStringInterpolation}
import models.Like
import play.api.db.Database
import repositories.DatabaseExecutionContext.databaseExecutionContext

import javax.inject.Inject
import scala.concurrent.Future

class LikeRepository @Inject()(db: Database){

  def like(userId: BigInt, postId: BigInt): Future[Boolean] = Future {
    val ID: Option[Long] = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO likes (userId, postId)
        VALUES ({userId}, {postId})
        """
      ).on(
        "userId" -> userId,
        "postId" -> postId
      ).executeInsert()
    }
    ID.isDefined
  }(databaseExecutionContext)

  private val likeParser: RowParser[Like] = Macro.namedParser[Like]

  def getLikeByUserAndPost(userId: BigInt, postId: BigInt): Future[Option[Like]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT *
        FROM likes
        WHERE userId = $userId AND postId = $postId
      """.as(likeParser.singleOpt)
    }
  }(databaseExecutionContext)

  def unlike(userId: BigInt, postId: BigInt): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         DELETE
         FROM likes
         WHERE userId = {userId} AND postId = {postId}
         """
      ).on(
        "userId" -> userId,
        "postId" -> postId
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  def likeCount(postId: BigInt): Future[Int] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT COUNT(*) AS likeCount
        FROM likes
        WHERE postId = $postId
      """.as(SqlParser.int("likeCount").singleOpt)
    }.getOrElse(0)
  }(databaseExecutionContext)

}
