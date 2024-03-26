package repositories

import anorm.{Macro, RowParser, SQL, SqlStringInterpolation}
import models.{FriendshipRequest, User, UserDetailsResponse}
import play.api.db.Database
import repositories.DatabaseExecutionContext.databaseExecutionContext
import anorm.SqlParser._
import anorm._

import javax.inject.Inject
import scala.concurrent.Future

class FriendshipRepository @Inject()(db: Database){

  def createFriendshipRequest(fromUserId: BigInt, forUserId: BigInt): Future[Boolean] = Future {
    val rowsAffected = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO friendship_request (forUser_id, fromUser_id, approved)
        VALUES ({forUser_id}, {fromUser_id}, {approved})
        """
      ).on(
        "forUser_id" -> forUserId,
        "fromUser_id" -> fromUserId,
        "approved" -> false,
      ).executeUpdate()
    }
    rowsAffected > 0
  }(databaseExecutionContext)

  def acceptRequest(requestId: BigInt): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         UPDATE friendship_request
         SET approved = {approved}
         WHERE id = {id}
         """
      ).on(
        "approved" -> true,
        "id" -> requestId
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  def denyRequest(requestId: BigInt): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         DELETE
         FROM friendship_request
         WHERE id = {id}
         """
      ).on(
        "id" -> requestId
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  def getFriendshipRequestById(id: BigInt): Future[Option[FriendshipRequest]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT fr.id, fr.approved, fr.forUser_id, u1.username AS forUsername, fr.fromUser_id, u2.username AS fromUsername
        FROM friendship_request fr
        JOIN user u1 ON fr.forUser_id = u1.id
        JOIN user u2 ON fr.fromUser_id = u2.id
        WHERE fr.id = $id
      """.as(friendshipRequestWithUsersParser.singleOpt)
    }
  }(databaseExecutionContext)

  def addFriend(userId: BigInt, friendId: BigInt): Future[Boolean] = Future {
    val rowsAffected = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO friendship (user_id, friend_id)
        VALUES ({user_id}, {friend_id})
        """
      ).on(
        "user_id" -> userId,
        "friend_id" -> friendId,
      ).executeUpdate()
    }
    rowsAffected > 0
  }(databaseExecutionContext)

  val friendshipRequestWithUsersParser: RowParser[FriendshipRequest] = {
    get[BigInt]("id") ~
      get[Boolean]("approved") ~
      get[BigInt]("forUser_id") ~
      get[String]("forUsername") ~
      get[BigInt]("fromUser_id") ~
      get[String]("fromUsername") map {
      case id ~ approved ~ forUserId ~ forUsername ~ fromUserId ~ fromUsername =>
        val forUser = UserDetailsResponse(forUserId, forUsername)
        val fromUser = UserDetailsResponse(fromUserId, fromUsername)
        FriendshipRequest(id, forUser, fromUser, approved)
    }
  }

  def getMyRequests(userId: BigInt): Future[List[FriendshipRequest]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT fr.id, fr.approved, fr.forUser_id, u1.username AS forUsername, fr.fromUser_id, u2.username AS fromUsername
        FROM friendship_request fr
        JOIN user u1 ON fr.forUser_id = u1.id
        JOIN user u2 ON fr.fromUser_id = u2.id
        WHERE fr.forUser_id = $userId AND fr.approved = false
      """.as(friendshipRequestWithUsersParser.*)
    }
  }(databaseExecutionContext)


}
