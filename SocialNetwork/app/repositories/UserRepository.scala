package repositories

import javax.inject.Inject
import anorm._
import play.api.db.Database
import models.{Gender, User}
import org.mindrot.jbcrypt.BCrypt
import anorm.SqlParser.{get, str}
import models.Gender.Gender

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

  private val userParser: RowParser[User] = {
        get[String]("name") ~
        get[String]("lastName") ~
        get[String]("username") ~
        get[String]("email") ~
        get[String]("password") ~
        get[LocalDate]("dateOfBirth") ~
        get[String]("phoneNumber") ~
        get[String]("gender") map {
      case name ~ lastName ~ username ~ email ~ password ~ dateOfBirth ~ phoneNumber ~ genderStr =>
        val gender = Gender.withName(genderStr)
        User(name, lastName, username, email, password, dateOfBirth, phoneNumber, gender)
    }
  }

  def getUserByUsername(username: String): Option[User] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM user WHERE username = $username".as(userParser.singleOpt)
    }
  }

}
