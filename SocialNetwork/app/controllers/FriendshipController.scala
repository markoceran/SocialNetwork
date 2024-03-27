package controllers

import helper.{TokenUtils, TokenValidationAction}
import models.{AcceptDenyRequest, CreateFriendshipRequest}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.FriendshipService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FriendshipController @Inject()(cc: ControllerComponents, friendshipService: FriendshipService, tokenValidationAction: TokenValidationAction)(implicit ec: ExecutionContext) extends AbstractController(cc)  {

  def createFriendshipRequest = tokenValidationAction.async(parse.json[CreateFriendshipRequest]) { request =>
    val createFriendshipRequest = request.body
    val fromUserIdOption = TokenUtils.getUserIdFromToken(request)
    fromUserIdOption.map { fromUserId =>
      friendshipService.createFriendshipRequest(fromUserId, createFriendshipRequest).map { success =>
        if (success) {
          Ok(Json.toJson("Friendship request created successfully"))
        } else {
          BadRequest(Json.toJson("Failed to create friendship request"))
        }
      }
    }.getOrElse {
      Future.successful(NotFound(Json.toJson("User id not found")))
    }
  }

  def acceptRequest = tokenValidationAction.async(parse.json[AcceptDenyRequest]) { request =>
    val acceptRequest = request.body
    friendshipService.acceptRequest(acceptRequest).map { success =>
        if (success) {
          Ok(Json.toJson("Friendship request accepted successfully"))
        } else {
          Forbidden(Json.toJson("Failed to accept friendship request"))
        }
    }
  }

  def denyRequest = tokenValidationAction.async(parse.json[AcceptDenyRequest]) { request =>
    val denyRequest = request.body
    friendshipService.denyRequest(denyRequest).map { success =>
      if (success) {
        Ok(Json.toJson("Friendship request denied successfully"))
      } else {
        Forbidden(Json.toJson("Failed to deny friendship request"))
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
      Future.successful(NotFound(Json.toJson("User id not found")))
    }
  }

}
