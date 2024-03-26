package controllers

import helper.{TokenUtils, TokenValidationAction}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{FriendshipService, UserService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FriendshipController @Inject()(cc: ControllerComponents, friendshipService: FriendshipService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc)  {

  def createFriendshipRequest = tokenValidationAction.async(parse.json) { request =>
    val jsonBody = request.body
    val forUserId = (jsonBody \ "forUserId").as[BigInt]
    val fromUserIdOption = TokenUtils.getUserIdFromToken(request)
    fromUserIdOption.map { fromUserId =>
      friendshipService.createFriendshipRequest(fromUserId, forUserId).map { success =>
        if (success) {
          Ok("Friendship request created successfully")
        } else {
          InternalServerError("Failed to create friendship request")
        }
      }
    }.getOrElse {
      Future.successful(NotFound("User id not found"))
    }
  }

  def acceptRequest = tokenValidationAction.async(parse.json) { request =>
    val jsonBody = request.body
    val requestId = (jsonBody \ "requestId").as[BigInt]
    friendshipService.acceptRequest(requestId).map { success =>
        if (success) {
          Ok("Friendship request accepted successfully")
        } else {
          InternalServerError("Failed to accept friendship request")
        }
    }
  }

  def denyRequest = tokenValidationAction.async(parse.json) { request =>
    val jsonBody = request.body
    val requestId = (jsonBody \ "requestId").as[BigInt]
    friendshipService.denyRequest(requestId).map { success =>
      if (success) {
        Ok("Friendship request denied successfully")
      } else {
        InternalServerError("Failed to deny friendship request")
      }
    }
  }

  def getMyRequests = tokenValidationAction.async(parse.anyContent) { request =>
    val userIdOption = TokenUtils.getUserIdFromToken(request)
    userIdOption.map { userId =>
      val requestsFuture = friendshipService.getMyRequests(userId)
      requestsFuture.map { requests =>
        Ok(Json.toJson(requests))
      }
    }.getOrElse {
      Future.successful(NotFound("User id not found"))
    }
  }

}
