package repositories

import javax.inject.Inject
import anorm._
import play.api.db.Database
import models.User

class UserRepository @Inject()(db: Database) {
  def createUser(user: User): Boolean = {
    val rowsAffected = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO user (name, lastName, username, email, password)
        VALUES ({name}, {lastName}, {username}, {email}, {password})
        """
      ).on(
        "name" -> user.name,
        "lastName" -> user.lastName,
        "username" -> user.username,
        "email" -> user.email,
        "password" -> user.password
      ).executeUpdate()
    }

    rowsAffected > 0
  }
}
