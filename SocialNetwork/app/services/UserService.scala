package services


import helper.TokenUtils

import javax.inject.Inject
import models.{LoginRequest, NewPasswordRequest, NewUsernameRequest, User, UserDetailsResponse}
import org.mindrot.jbcrypt.BCrypt
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}


class UserService @Inject()(userRepository: UserRepository) (implicit ec: ExecutionContext){

  def createUser(user: User): Future[(Boolean, String)] = {
    validateUsername(user.username).flatMap { usernameValid =>
      if (!usernameValid) {
        Future.successful(false, "User with entered username already exists")
      } else if (!validatePassword(user.password)) {
        Future.successful(false, "Invalid password format. Password must contain at least 11 characters, an uppercase initial letter, and at least one special character")
      } else if (!user.username.nonEmpty || !user.password.nonEmpty) {
        Future.successful(false, "All fields are required")
      } else {
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val userWithHashedPassword = user.copy(password = hashedPassword)
        userRepository.createUser(userWithHashedPassword).map(created => (created, ""))
      }
    }
  }

  def getUserByUsername(username: String): Future[Option[UserDetailsResponse]] = {
    userRepository.getUserByUsername(username).map {
      case Some(user)=>
        val userDetails: UserDetailsResponse = UserDetailsResponse(user.id, user.username)
        Some(userDetails)
      case None => None
    }
  }

  def changePassword(username: String, newPasswordRequest: NewPasswordRequest):  Future[Boolean] = {
      if ((newPasswordRequest.newPassword == newPasswordRequest.newPasswordAgain) && validatePassword(newPasswordRequest.newPassword)) {
        val hashedPassword = BCrypt.hashpw(newPasswordRequest.newPassword, BCrypt.gensalt())
        userRepository.changePassword(username, hashedPassword)
      }
      else{
        Future.successful(false)
      }
  }

  def changeUsername(username: String, newUsernameRequest: NewUsernameRequest): Future[Boolean] = {
    userRepository.getUserByUsername(newUsernameRequest.username).flatMap {
      case Some(_) =>
        Future.successful(false)
      case None =>
        userRepository.changeUsername(username, newUsernameRequest.username)
    }
  }

  def login(loginRequest: LoginRequest): Future[String] = {
    userRepository.getUserByUsername(loginRequest.username).map {
      case Some(user) =>
        if (BCrypt.checkpw(loginRequest.password, user.password)) {
          TokenUtils.generateJwtToken(user.username, user.id)
        } else {
          ""
        }
      case None => ""
    }
  }
  private def validatePassword(password: String): Boolean = {
    val isLengthValid = password.length >= 11
    val containsUpperCase = password.exists(_.isUpper)
    val containsSpecialChar = password.exists(ch => "!@#$%^&*()-+=\\|[]{};:'\",.<>/?".contains(ch))
    isLengthValid && containsUpperCase && containsSpecialChar
  }
  private def validateUsername(username: String): Future[Boolean] = {
    userRepository.getUserByUsername(username).map {
      case Some(_) => false
      case None => true
    }
  }
}
