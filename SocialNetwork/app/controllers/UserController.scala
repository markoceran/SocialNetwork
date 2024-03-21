package controllers

import javax.inject._
import play.api.mvc._
import models.User
import org.mindrot.jbcrypt.BCrypt
import services.UserService
import scala.util.matching.Regex
import javax.inject.Inject
import play.api.libs.json.Json
import helper.TokenUtils

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService) extends AbstractController(cc) {

  def createUser = Action(parse.json) { request =>
    // todo: use parse.json[User] to validate json format
    request.body.validate[User].fold(
      errors => {
        BadRequest("Invalid JSON")
      },
      user => {
        // todo: move business logic validation into services
        //       what if username is already taken?
        //       email? name? lastname???
        if(!validateEmail(user.email)){
          BadRequest("Invalid email format")
        }else if(!validatePassword(user.password)){
          BadRequest("Invalid password format. Password must contain at least 11 characters, an uppercase initial letter and at least one special character")
        }else if(!user.name.nonEmpty || !user.lastName.nonEmpty || !user.username.nonEmpty || !user.email.nonEmpty  || !user.password.nonEmpty  || !user.phoneNumber.nonEmpty){
          BadRequest("All fields are required")
        }else{
          val userAdded = userService.createUser(user)
          if (userAdded) {
            Ok("User added successfully")
          } else {
            InternalServerError("Failed to add user")
          }
        }

      }
    )
  }

  def getUserByUsername(username: String) = Action { implicit request =>

    // todo: move JWT auth stuff to custom action 
    val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
    val (isValid, isExpired) = TokenUtils.validateJwtToken(token)

    if (isValid) {
      if (isExpired) {
        Unauthorized("Token is expired")
      } else {
        val user = userService.getUserByUsername(username)
        user match {
          case Some(user) => Ok(Json.toJson(user))
          case None => NotFound("User not found")
        }
      }
    } else {
      Unauthorized("Invalid token")
    }

  }


  private def validatePassword(password: String): Boolean = {

    val isLengthValid = password.length >= 11
    val containsUpperCase = password.exists(_.isUpper)

    // Check if the password contains at least one special character
    val containsSpecialChar = password.exists(ch => "!@#$%^&*()-+=\\|[]{};:'\",.<>/?".contains(ch))

    isLengthValid && containsUpperCase && containsSpecialChar
  }

  private def validateEmail(email: String): Boolean = {
    // Regular expression pattern for validating email addresses
    val emailPattern: Regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r

    // Check if the email matches the pattern
    email match {
      case emailPattern() => true
      case _ => false
    }
  }

  def login = Action(parse.json) { implicit request =>
    // todo: use 'DTO' ?
    //       fe. case class LoginRequest(username: String, password: String)
    val json = request.body
    val username = (json \ "username").as[String]
    val password = (json \ "password").as[String]

    val user = userService.getUserByUsername(username)

    user match {
      case Some(user) =>
        // todo: move business logic to auth service/user service
        if (BCrypt.checkpw(password, user.password)) {
          val token = TokenUtils.generateJwtToken(user.username)
          Ok(token)
        } else {
          Unauthorized("Invalid username or password")
        }
      case None =>
        NotFound("User not found")
    }
  }

  def updateUser = Action(parse.json) { implicit request =>

    val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
    val (isValid, isExpired) = TokenUtils.validateJwtToken(token)

    if (isValid) {
      if (isExpired) {
        Unauthorized("Token is expired")
      } else {
        request.body.validate[User].fold(
          errors => {
            BadRequest("Invalid JSON")
          },
          updatedUserData => {
            val username = TokenUtils.getUsernameFromToken(token)
            username match {
              case Some(username) =>
                val existingUserOpt = userService.getUserByUsername(username)
                existingUserOpt match {
                  case Some(existingUser) =>
                    // Update only the fields that are present in the updated data
                    val updatedUser = existingUser.copy(
                      name = if (updatedUserData.name != null) updatedUserData.name else existingUser.name,
                      lastName = if (updatedUserData.lastName != null) updatedUserData.lastName else existingUser.lastName,
                      email = if (updatedUserData.email != null) updatedUserData.email else existingUser.email,
                      phoneNumber = if (updatedUserData.phoneNumber != null) updatedUserData.phoneNumber else existingUser.phoneNumber,
                      dateOfBirth = if (updatedUserData.dateOfBirth != null) updatedUserData.dateOfBirth else existingUser.dateOfBirth
                    )
                    val userUpdated = userService.updateUser(username, updatedUser)
                    if (userUpdated) {
                      Ok("User updated successfully")
                    } else {
                      InternalServerError("Failed to update user")
                    }
                  case None =>
                    NotFound("User not found")
                }
              case None =>
                Unauthorized("Invalid token")
            }
          }
        )
      }
    } else {
      Unauthorized("Invalid token")
    }

  }


}
