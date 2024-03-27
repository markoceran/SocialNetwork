package controllers

import javax.inject._
import play.api.mvc._
import models.{LoginRequest, NewPasswordRequest, NewUsernameRequest, User}
import services.UserService

import javax.inject.Inject
import play.api.libs.json.Json
import helper.{TokenValidationAction}

import scala.concurrent.{ExecutionContext}


@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def createUser = Action.async(parse.json[User]) { request =>
    val user: User = request.body
    userService.createUser(user).map {
      case (true, _) => Ok(Json.obj("message"->"User added successfully"))
      case (false, message) => BadRequest(Json.obj("message"->message))
    }
  }

  def getUserByUsername(username: String) = tokenValidationAction.async(parse.anyContent) { _ =>
    val userFuture = userService.getUserByUsername(username)
    userFuture.map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message"->"User not found"))
    }
  }

  def login = Action.async(parse.json[LoginRequest]) { request =>
    val loginRequest: LoginRequest = request.body
    userService.login(loginRequest).map { token =>
      if (token.isEmpty) Unauthorized(Json.obj("message"->"Invalid username or password"))
      else Ok(Json.obj("token"->token))
    }
  }

  def changePassword = tokenValidationAction.async(parse.json[NewPasswordRequest]) { request =>
    val newPasswordRequest: NewPasswordRequest = request.body
    val username: String = request.username
    userService.changePassword(username, newPasswordRequest).map { success =>
      if (success) {
        Ok(Json.obj("message"->"Password updated successfully"))
      } else {
        Forbidden(Json.obj("message"->"Failed to update password"))
      }
    }
  }

  def changeUsername= tokenValidationAction.async(parse.json[NewUsernameRequest]) { request =>
    val newUsernameRequest = request.body
    val username: String = request.username
    userService.changeUsername(username, newUsernameRequest).map { success =>
      if (success) {
        Ok(Json.obj("message"->"Username updated successfully"))
      } else {
        Forbidden(Json.obj("message"->"Failed to update username"))
      }
    }
  }
}


