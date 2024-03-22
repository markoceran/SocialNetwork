package controllers

import javax.inject._
import play.api.mvc._
import models.{LoginRequest, User}
import services.UserService
import javax.inject.Inject
import play.api.libs.json.Json
import helper.TokenUtils
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def createUser = Action.async(parse.json[User]) { request =>
    val user: User = request.body
    userService.createUser(user).map {
      case (true, _) => Ok("User added successfully")
      case (false, message) => BadRequest(message)
    }
  }

  def getUserByUsername(username: String) = Action.async(parse.anyContent) { request =>
    val isValid = TokenUtils.validateJwtToken(request)
    if (isValid) {
          val userFuture = userService.getUserByUsername(username)
          userFuture.map {
            case Some(user) => Ok(Json.toJson(user))
            case None => NotFound("User not found")
          }
    } else {
      Future.successful(Unauthorized("Invalid token"))
    }
  }

  def login = Action.async(parse.json[LoginRequest]) { implicit request =>
    val loginRequest: LoginRequest = request.body
    userService.login(loginRequest).map { token =>
      if (token.isEmpty) Unauthorized("Invalid username or password")
      else Ok(token)
    }
  }
}

//  def updateUser = Action(parse.json) { implicit request =>
//
//    val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
//    val (isValid, isExpired) = TokenUtils.validateJwtToken(token)
//
//    if (isValid) {
//      if (isExpired) {
//        Unauthorized("Token is expired")
//      } else {
//        request.body.validate[User].fold(
//          errors => {
//            BadRequest("Invalid JSON")
//          },
//          updatedUserData => {
//            val username = TokenUtils.getUsernameFromToken(token)
//            username match {
//              case Some(username) =>
//                val existingUserOpt = userService.getUserByUsername(username)
//                existingUserOpt match {
//                  case Some(existingUser) =>
//                    // Update only the fields that are present in the updated data
//                    val updatedUser = existingUser.copy(
//                      name = if (updatedUserData.name != null) updatedUserData.name else existingUser.name,
//                      lastName = if (updatedUserData.lastName != null) updatedUserData.lastName else existingUser.lastName,
//                      email = if (updatedUserData.email != null) updatedUserData.email else existingUser.email,
//                      phoneNumber = if (updatedUserData.phoneNumber != null) updatedUserData.phoneNumber else existingUser.phoneNumber,
//                      dateOfBirth = if (updatedUserData.dateOfBirth != null) updatedUserData.dateOfBirth else existingUser.dateOfBirth
//                    )
//                    val userUpdated = userService.updateUser(username, updatedUser)
//                    if (userUpdated) {
//                      Ok("User updated successfully")
//                    } else {
//                      InternalServerError("Failed to update user")
//                    }
//                  case None =>
//                    NotFound("User not found")
//                }
//              case None =>
//                Unauthorized("Invalid token")
//            }
//          }
//        )
//      }
//    } else {
//      Unauthorized("Invalid token")
//    }
//
//  }



