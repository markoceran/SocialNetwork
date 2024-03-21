package repositories

import javax.inject.Inject
import anorm._
import play.api.db.Database
import models.{Gender, User}
import org.mindrot.jbcrypt.BCrypt
import anorm.SqlParser.{get, str}
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDate


class UserRepository @Inject()(db: Database) {
  def createUser(user: User): Boolean = {
    val rowsAffected = db.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO user (name, lastName, username, email, password, dateOfBirth, phoneNumber, gender)
        VALUES ({name}, {lastName}, {username}, {email}, {password}, {dateOfBirth}, {phoneNumber}, {gender})
        """
      ).on(
        "name" -> user.name,
        "lastName" -> user.lastName,
        "username" -> user.username,
        "email" -> user.email,
        "password" -> BCrypt.hashpw(user.password, BCrypt.gensalt()),
        "dateOfBirth" -> user.dateOfBirth,
        "phoneNumber" -> user.phoneNumber,
        "gender" -> user.gender.toString
      ).executeUpdate()
    }

    rowsAffected > 0
  }

  private val userParser: RowParser[User] =
  {
        get[BigInt]("id") ~
        get[String]("name") ~
        get[String]("lastName") ~
        get[String]("username") ~
        get[String]("email") ~
        get[String]("password") ~
        get[LocalDate]("dateOfBirth") ~
        get[String]("phoneNumber") ~
        get[String]("gender") map {
      case id ~ name ~ lastName ~ username ~ email ~ password ~ dateOfBirth ~ phoneNumber ~ genderStr =>
        val gender = Gender.withName(genderStr)
        User(id ,name, lastName, username, email, password, dateOfBirth, phoneNumber, gender)
    }
  }

  def getUserByUsername(username: String): Option[User] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM user WHERE username = $username".as(userParser.singleOpt)
    }
  }

  def updateUser(username: String, updatedUser: User): Boolean = {
    val query =
      """
        | UPDATE user
        | SET name = ?, lastName = ?, email = ?, phoneNumber = ?, dateOfBirth = ?
        | WHERE username = ?
        """.stripMargin

    var connection: Connection = null
    var preparedStatement: PreparedStatement = null

    try {
      connection = db.getConnection()
      preparedStatement = connection.prepareStatement(query)

      preparedStatement.setString(1, updatedUser.name)
      preparedStatement.setString(2, updatedUser.lastName)
      preparedStatement.setString(3, updatedUser.email)
      preparedStatement.setString(4, updatedUser.phoneNumber)
      preparedStatement.setDate(5, java.sql.Date.valueOf(updatedUser.dateOfBirth))
      preparedStatement.setString(6, username)

      val rowsAffected = preparedStatement.executeUpdate()

      rowsAffected > 0
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        false
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close()
      }
      if (connection != null) {
        connection.close()
      }
    }
  }

}
