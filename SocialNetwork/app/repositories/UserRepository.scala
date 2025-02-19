package repositories

import javax.inject.Inject
import anorm._
import play.api.db.Database
import models.{User, UserDetailsResponse}
import repositories.DatabaseExecutionContext.databaseExecutionContext

import scala.concurrent.Future
import anorm.{Macro, RowParser}

class UserRepository @Inject()(db: Database){

  def createUser(user: User): Future[Boolean] = Future {
    val ID: Option[Long] = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO user (username, password)
        VALUES ({username}, {password})
        """
      ).on(
        "username" -> user.username,
        "password" -> user.password,
      ).executeInsert()
    }
    ID match {
      case Some(_) => true
      case None => false
    }
  }(databaseExecutionContext)


  private val userParser: RowParser[User] = Macro.namedParser[User]
  def getUserByUsername(username: String): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM user WHERE username = $username".as(userParser.singleOpt)
    }
  }(databaseExecutionContext)

  def changePassword(username: String, newPassword: String): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         UPDATE user
         SET password = {newPassword}
         WHERE username = {username}
         """
      ).on(
        "newPassword" -> newPassword,
        "username" -> username
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  def changeUsername(username: String, newUsername: String): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         UPDATE user
         SET username = {newUsername}
         WHERE username = {username}
         """
      ).on(
        "newUsername" -> newUsername,
        "username" -> username
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

  private val userWithoutPasswordParser: RowParser[UserDetailsResponse] = Macro.namedParser[UserDetailsResponse]

  def getMyFriends(userId: BigInt): Future[List[UserDetailsResponse]] = Future {
    db.withConnection { implicit connection =>
      SQL"""
        SELECT u1.id, u1.username
        FROM friendship f
        JOIN user u1 ON f.friend_id = u1.id
        WHERE f.user_id = $userId
      """.as(userWithoutPasswordParser.*)
    }
  }(databaseExecutionContext)

  def searchUsersByPrefix(prefix: String, pageNumber: Int, pageSize: Int): Future[List[UserDetailsResponse]] = Future {
    db.withConnection { implicit connection =>
      SQL"SELECT id, username FROM user WHERE username LIKE ${prefix + "%"} LIMIT $pageSize OFFSET ${(pageNumber - 1) * pageSize}".as(userWithoutPasswordParser.*)
    }
  }(databaseExecutionContext)

}
