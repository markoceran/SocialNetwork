package repositories

import anorm.SqlParser.get
import anorm.{RowParser, SQL, SqlStringInterpolation, ~}
import models.{Post, UserDetailsResponse}
import play.api.db.Database
import repositories.DatabaseExecutionContext.databaseExecutionContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future

class PostRepository @Inject()(db: Database){

  def createPost(postedById: BigInt, content: String): Future[Boolean] = Future {
    val ID: Option[Long] = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO post (content, postedByUser_id, creation_date, edited)
        VALUES ({content}, {postedByUser_id}, {creation_date}, {edited})
        """
      ).on(
        "content" -> content,
        "postedByUser_id" -> postedById,
        "creation_date" -> LocalDateTime.now(),
        "edited" -> false,
      ).executeInsert()
    }
    ID.isDefined
  }(databaseExecutionContext)

  def editPost(postId: BigInt, newContent: String): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         UPDATE post
         SET content = {newContent}, edited = {edited}
         WHERE id = {postId}
         """
      ).on(
        "newContent" -> newContent,
        "edited" -> true,
        "postId" -> postId
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  val postWithUserParser: RowParser[Post] = {
    get[BigInt]("id") ~
      get[String]("content") ~
      get[LocalDateTime]("creation_date") ~
      get[Boolean]("edited") ~
      get[BigInt]("postedByUser_id") ~
      get[String]("postedByUsername") map {
      case id ~ content ~ creationDate ~ edited ~ postedById ~ postedByUsername =>
        val postedBy = UserDetailsResponse(postedById, postedByUsername)
        Post(id, content, postedBy, creationDate, edited)
    }
  }

  def getPostById(id: BigInt): Future[Option[Post]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT p.id, p.content, p.postedByUser_id, p.creation_date, p.edited, u.username AS postedByUsername
        FROM post p
        JOIN user u ON p.postedByUser_id = u.id
        WHERE p.id = $id
      """.as(postWithUserParser.singleOpt)
    }
  }(databaseExecutionContext)

  def deletePost(id: BigInt): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         DELETE
         FROM post
         WHERE id = {id}
         """
      ).on(
        "id" -> id
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  def getPostsByUser(username: String, pageNumber: Int, pageSize: Int): Future[List[Post]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT p.id, p.content, p.postedByUser_id, p.creation_date, p.edited, u.username AS postedByUsername
        FROM post p
        JOIN user u ON p.postedByUser_id = u.id
        WHERE u.username = $username ORDER BY p.creation_date DESC
        LIMIT $pageSize OFFSET ${(pageNumber - 1) * pageSize}
      """.as(postWithUserParser.*)
    }
  }(databaseExecutionContext)

  def getMyFriendsPosts(userId: BigInt, pageNumber: Int, pageSize: Int): Future[List[Post]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT p.id, p.content, p.postedByUser_id, p.creation_date, p.edited, u.username AS postedByUsername
        FROM post p
        JOIN user u ON p.postedByUser_id = u.id
        JOIN friendship f ON p.postedByUser_id = f.friend_id
        WHERE f.user_id = $userId ORDER BY p.creation_date DESC
        LIMIT $pageSize OFFSET ${(pageNumber - 1) * pageSize}
      """.as(postWithUserParser.*)
    }
  }(databaseExecutionContext)

}
