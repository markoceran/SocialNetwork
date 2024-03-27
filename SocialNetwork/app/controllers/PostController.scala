package controllers

import helper.TokenValidationAction
import models.{NewPost, Post}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.PostService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PostController @Inject()(cc: ControllerComponents, postService: PostService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def createPost = tokenValidationAction.async(parse.json[NewPost]) { request =>
    val newPost: NewPost = request.body
    val userId: BigInt = request.userId
    postService.createPost(userId, newPost).map {
      case (true, _) => Ok(Json.obj("message" -> "Post created successfully"))
      case (false, message) => BadRequest(Json.obj("message" -> message))
    }
  }

}
