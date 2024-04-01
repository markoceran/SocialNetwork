package controllers

import helper.TokenValidationAction
import models.NewLike
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.LikeService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LikeController  @Inject()(cc: ControllerComponents, likeService: LikeService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def addLike = tokenValidationAction.async(parse.json[NewLike]) { request =>
    val newLike: NewLike = request.body
    val userId: BigInt = request.userId
    likeService.addLike(userId, newLike).map {
      case (true, _) => Ok(Json.obj("message" -> "Like added successfully"))
      case (false, message) => BadRequest(Json.obj("message" -> message))
    }
  }

}
