package services

import models.LikeRequest
import repositories.{FriendshipRepository, LikeRepository, PostRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LikeService @Inject()(likeRepository: LikeRepository, postRepository: PostRepository, friendshipRepository: FriendshipRepository) (implicit ec: ExecutionContext){

  def like(userId: BigInt, newLike: LikeRequest): Future[(Boolean, String)] = {
    postRepository.getPostById(newLike.postId).flatMap {
      case Some(_) =>
        likeRepository.getLikeByUserAndPost(userId, newLike.postId).flatMap{
          case Some(_) => Future.successful(false, "You already liked this post")
          case None =>
            postRepository.getPostedBy(newLike.postId).flatMap{
              case Some(postPostedBy) =>
                friendshipRepository.weAreFriends(userId, postPostedBy).flatMap {
                  case true => likeRepository.like(userId, newLike.postId).map(likeSuccess => (likeSuccess, ""))
                  case false => Future.successful(false, "You can't like this post")
                }
              case None => Future.successful(false, "Unexpected error")
            }
        }
      case None => Future.successful(false, "Post doesn't exist")
    }
  }

  def unlike(userId: BigInt, like: LikeRequest): Future[(Boolean, String)] = {
    likeRepository.getLikeByUserAndPost(userId, like.postId).flatMap {
      case Some(_) => likeRepository.unlike(userId, like.postId).map(unlikeSuccess => (unlikeSuccess, ""))
      case None => Future.successful(false, "Like doesn't exist")
    }
  }

}
