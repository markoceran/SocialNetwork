package services

import models.FriendshipRequest
import repositories.{FriendshipRepository, UserRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FriendshipService @Inject()(friendshipRepository: FriendshipRepository, userRepository: UserRepository) (implicit ec: ExecutionContext){

  def createFriendshipRequest(fromUserId: BigInt, forUserId: BigInt): Future[Boolean] = {
     if (forUserId >= 0 ) {
        friendshipRepository.createFriendshipRequest(fromUserId, forUserId)
     } else {
       Future.successful(false)
     }
  }

  def acceptRequest(requestId: BigInt): Future[Boolean] = {
    if (requestId >= 0) {
      friendshipRepository.acceptRequest(requestId)
      val requestFuture = friendshipRepository.getFriendshipRequestById(requestId)
      requestFuture.flatMap {
        case Some(request) =>
          friendshipRepository.addFriend(request.fromUser.id, request.forUser.id)
          friendshipRepository.addFriend(request.forUser.id, request.fromUser.id)
        case None => Future.successful(false)
      }

    } else {
      Future.successful(false)
    }
  }

  def denyRequest(requestId: BigInt): Future[Boolean] = {
    if (requestId >= 0) {
      val requestFuture = friendshipRepository.getFriendshipRequestById(requestId)
      requestFuture.flatMap {
        case Some(request) =>
          if(request.approved == false){
            friendshipRepository.denyRequest(requestId)
          }else{
            Future.successful(false)
          }
        case None => Future.successful(false)
      }
    } else {
      Future.successful(false)
    }
  }

  def getMyRequests(userId: BigInt): Future[List[FriendshipRequest]] = {
    friendshipRepository.getMyRequests(userId)
  }

}
