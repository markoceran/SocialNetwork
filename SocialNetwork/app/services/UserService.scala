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
        Future.successful(false, "User with entered username already exists or entered username contains more than 20 characters")
      } else if (!validatePassword(user.password)) {
        Future.successful(false, "Invalid password format. Password must contain at least 11 characters, no more than 80 characters, an uppercase initial letter, and at least one special character")
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

  def changePassword(username: String, newPasswordRequest: NewPasswordRequest): Future[Boolean] = {
    if (newPasswordRequest.newPassword == newPasswordRequest.newPasswordAgain && validatePassword(newPasswordRequest.newPassword)) {
      val currentPasswordValidationFuture = validateCurrentPassword(username, newPasswordRequest.currentPassword)
      currentPasswordValidationFuture.flatMap { isValid =>
        if (isValid) {
          val hashedPassword = BCrypt.hashpw(newPasswordRequest.newPassword, BCrypt.gensalt())
          userRepository.changePassword(username, hashedPassword)
        } else {
          Future.successful(false)
        }
      }
    } else {
      Future.successful(false)
    }
  }

  def changeUsername(username: String, newUsernameRequest: NewUsernameRequest): Future[Boolean] = {
    validateUsername(newUsernameRequest.newUsername).flatMap {
      case false =>
        Future.successful(false)
      case true =>
        userRepository.changeUsername(username, newUsernameRequest.newUsername)
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
    val isLengthValidMin = password.length >= 11
    val isLengthValidMax = password.length <= 80
    val containsUpperCase = password.exists(_.isUpper)
    val containsSpecialChar = password.exists(ch => "!@#$%^&*()-+=\\|[]{};:'\",.<>/?".contains(ch))
    isLengthValidMin && isLengthValidMax && containsUpperCase && containsSpecialChar
  }
  private def validateUsername(username: String): Future[Boolean] = {
    if(username.length <= 20){
      userRepository.getUserByUsername(username).map {
        case Some(_) => false
        case None => true
      }
    } else {
      Future.successful(false)
    }
  }
  private def validateCurrentPassword(username: String, currentPassword: String): Future[Boolean] = {
    userRepository.getUserByUsername(username).map {
      case Some(user) =>
        BCrypt.checkpw(currentPassword, user.password)
      case None => false
    }
  }
}
