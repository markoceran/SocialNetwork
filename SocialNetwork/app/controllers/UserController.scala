package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.User
import services.UserService

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService) extends AbstractController(cc) {

  def createUser = Action(parse.json) { request =>
    request.body.validate[User].fold(
      errors => {
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid JSON"))
      },
      user => {
        val userAdded = userService.createUser(user)
        if (userAdded) {
          Ok("User added successfully")
        } else {
          InternalServerError(Json.obj("status" -> "error", "message" -> "Failed to add user"))
        }
      }
    )
  }
}
