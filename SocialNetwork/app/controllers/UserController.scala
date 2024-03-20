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
    request.body.validate[User].fold(
      errors => {
        BadRequest("Invalid JSON")
      },
      user => {
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
    val json = request.body
    val username = (json \ "username").as[String]
    val password = (json \ "password").as[String]

    val user = userService.getUserByUsername(username)

    user match {
      case Some(user) =>
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

}
