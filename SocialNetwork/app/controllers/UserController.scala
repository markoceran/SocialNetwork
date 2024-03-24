package controllers

import javax.inject._
import play.api.mvc._
import models.{LoginRequest, NewPasswordRequest, NewUsernameRequest, User}
import services.UserService

import javax.inject.Inject
import play.api.libs.json.Json
import helper.{TokenUtils, TokenValidationAction}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def createUser = Action.async(parse.json[User]) { request =>
    val user: User = request.body
    userService.createUser(user).map {
      case (true, _) => Ok("User added successfully")
      case (false, message) => BadRequest(message)
    }
  }

  def getUserByUsername(username: String) = tokenValidationAction.async(parse.anyContent) { request =>
    val userFuture = userService.getUserByUsername(username)
    userFuture.map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound("User not found")
    }
  }

  def login = Action.async(parse.json[LoginRequest]) { implicit request =>
    val loginRequest: LoginRequest = request.body
    userService.login(loginRequest).map { token =>
      if (token.isEmpty) Unauthorized("Invalid username or password")
      else Ok(token)
    }
  }

  def changePassword = tokenValidationAction.async(parse.json[NewPasswordRequest]) { request =>
    val newPasswordRequest: NewPasswordRequest = request.body
    val usernameOption: Option[String] = TokenUtils.getUsernameFromToken(request)
    usernameOption.map { username =>
        userService.changePassword(username, newPasswordRequest).map { success =>
          if (success) {
            Ok("Password updated successfully")
          } else {
            InternalServerError("Failed to update password")
          }
        }
    }.getOrElse {
        Future.successful(NotFound("User not found"))
    }
  }

  def changeUsername= tokenValidationAction.async(parse.json[NewUsernameRequest]) { request =>
    val newUsernameRequest: NewUsernameRequest = request.body
    val usernameOption: Option[String] = TokenUtils.getUsernameFromToken(request)
      usernameOption.map { username =>
        userService.changeUsername(username, newUsernameRequest).map { success =>
          if (success) {
            Ok("Username updated successfully")
          } else {
            InternalServerError("Failed to update username")
          }
        }
      }.getOrElse {
        Future.successful(NotFound("User not found"))
      }
  }
}


