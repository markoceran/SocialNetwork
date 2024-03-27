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
    val fromUserId: BigInt = request.userId
    friendshipService.createFriendshipRequest(fromUserId, createFriendshipRequest).map { success =>
      if (success) {
        Ok(Json.obj("message"->"Friendship request created successfully"))
      } else {
        BadRequest(Json.obj("message"->"Failed to create friendship request"))
      }
    }
  }

  def acceptRequest = tokenValidationAction.async(parse.json[AcceptDenyRequest]) { request =>
    val acceptRequest = request.body
    friendshipService.acceptRequest(acceptRequest).map { success =>
        if (success) {
          Ok(Json.obj("message"->"Friendship request accepted successfully"))
        } else {
          Forbidden(Json.obj("message"->"Failed to accept friendship request"))
        }
    }
  }

  def denyRequest = tokenValidationAction.async(parse.json[AcceptDenyRequest]) { request =>
    val denyRequest = request.body
    friendshipService.denyRequest(denyRequest).map { success =>
      if (success) {
        Ok(Json.obj("message"->"Friendship request denied successfully"))
      } else {
        Forbidden(Json.obj("message"->"Failed to deny friendship request"))
      }
    }
  }

  def getMyRequests = tokenValidationAction.async(parse.anyContent) { request =>
    val userId: BigInt = request.userId
    val requestsFuture = friendshipService.getMyRequests(userId)
    requestsFuture.map { requests =>
      Ok(Json.toJson(requests))
    }
  }

}
