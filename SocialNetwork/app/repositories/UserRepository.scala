package repositories

import javax.inject.Inject
import anorm._
import play.api.db.Database
import models.User
import repositories.DatabaseExecutionContext.databaseExecutionContext

import scala.concurrent.{ExecutionContext, Future}
import anorm.{Macro, RowParser}

class UserRepository @Inject()(db: Database) (implicit ec: ExecutionContext){

  def createUser(user: User): Future[Boolean] = Future {
    val rowsAffected = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO user (username, password)
        VALUES ({username}, {password})
        """
      ).on(
        "username" -> user.username,
        "password" -> user.password,
      ).executeUpdate()
    }
    rowsAffected > 0
  }(databaseExecutionContext)


  private val userParser: RowParser[User] = Macro.namedParser[User]
  def getUserByUsername(username: String): Future[Option[User]] = Future {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM user WHERE username = $username".as(userParser.singleOpt)
    }
  }(databaseExecutionContext)

  def updateUser(username: String, updatedUser: User): Future[Boolean] = Future {
    db.withConnection { implicit connection =>
      val rowsAffected = SQL(
        """
         UPDATE user
         SET username = {newUsername}, password = {newPassword}
         WHERE username = {username}
         """
      ).on(
        "newUsername" -> updatedUser.username,
        "newPassword" -> updatedUser.password,
        "username" -> username
      ).executeUpdate()

      rowsAffected > 0
    }
  }(databaseExecutionContext)

}
