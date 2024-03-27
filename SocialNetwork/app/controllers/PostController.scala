package controllers

import helper.TokenValidationAction
import models.{NewPost, UpdatePost}
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

  def getPostById(id: String) = tokenValidationAction.async(parse.anyContent) { _ =>
    val postFuture = postService.getPostById(BigInt(id))
    postFuture.map {
      case Some(post) => Ok(Json.toJson(post))
      case None => NotFound(Json.obj("message" -> "Post not found"))
    }
  }
  def editPost = tokenValidationAction.async(parse.json[UpdatePost]) { request =>
    val updatePost: UpdatePost = request.body
    val userId: BigInt = request.userId
    postService.editPost(userId, updatePost).map {
      case (true, _) => Ok(Json.obj("message" -> "Post updated successfully"))
      case (false, message) => BadRequest(Json.obj("message" -> message))
    }
  }
}
