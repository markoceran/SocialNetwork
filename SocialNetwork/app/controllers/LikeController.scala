package controllers

import helper.TokenValidationAction
import models.LikeRequest
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.LikeService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LikeController  @Inject()(cc: ControllerComponents, likeService: LikeService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def like = tokenValidationAction.async(parse.json[LikeRequest]) { request =>
    val newLike: LikeRequest = request.body
    val userId: BigInt = request.userId
    likeService.like(userId, newLike).map {
      case (true, _) => Ok(Json.obj("message" -> "Like added successfully"))
      case (false, message) => BadRequest(Json.obj("message" -> message))
    }
  }

  def unlike = tokenValidationAction.async(parse.json[LikeRequest]) { request =>
    val like: LikeRequest = request.body
    val userId: BigInt = request.userId
    likeService.unlike(userId, like).map {
      case (true, _) => Ok(Json.obj("message" -> "Like deleted successfully"))
      case (false, message) => BadRequest(Json.obj("message" -> message))
    }
  }

}
