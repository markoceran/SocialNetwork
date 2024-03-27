package services

import models.{AcceptDenyRequest, CreateFriendshipRequest, FriendshipRequest}
import repositories.FriendshipRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FriendshipService @Inject()(friendshipRepository: FriendshipRepository) (implicit ec: ExecutionContext){

  def createFriendshipRequest(fromUserId: BigInt, createFriendshipRequest: CreateFriendshipRequest): Future[Boolean] = {
    if(fromUserId != createFriendshipRequest.forUserId){
      val requestFuture = friendshipRepository.getFriendshipRequestBetweenUsers(createFriendshipRequest.forUserId, fromUserId)
      requestFuture.flatMap {
        case Some(_) => Future.successful(false)
        case None => friendshipRepository.createFriendshipRequest(fromUserId, createFriendshipRequest.forUserId)
      }
    }else {
      Future.successful(false)
    }
  }

  def acceptRequest(acceptRequest: AcceptDenyRequest): Future[Boolean] = {
      val requestFuture = friendshipRepository.getFriendshipRequestById(acceptRequest.requestId)
      requestFuture.flatMap {
        case Some(request) =>
          if(!request.approved){
            friendshipRepository.acceptRequest(acceptRequest.requestId)
            friendshipRepository.addFriend(request.fromUser.id, request.forUser.id)
            friendshipRepository.addFriend(request.forUser.id, request.fromUser.id)
          }else {
            Future.successful(false)
          }
        case None => Future.successful(false)
      }
  }

  def denyRequest(denyRequest: AcceptDenyRequest): Future[Boolean] = {
      val requestFuture = friendshipRepository.getFriendshipRequestById(denyRequest.requestId)
      requestFuture.flatMap {
        case Some(request) =>
          if(!request.approved){
            friendshipRepository.denyRequest(denyRequest.requestId)
          }else{
            Future.successful(false)
          }
        case None => Future.successful(false)
      }
  }

  def getMyRequests(userId: BigInt): Future[List[FriendshipRequest]] = {
    friendshipRepository.getMyRequests(userId)
  }

}
