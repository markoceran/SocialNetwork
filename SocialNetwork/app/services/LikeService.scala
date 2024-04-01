package services

import models.NewLike
import repositories.{LikeRepository, PostRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LikeService @Inject()(likeRepository: LikeRepository, postRepository: PostRepository) (implicit ec: ExecutionContext){

  def addLike(userId: BigInt, newLike: NewLike): Future[(Boolean, String)] = {
    postRepository.getPostById(newLike.postId).flatMap {
      case Some(_) =>
        likeRepository.getLikeByUserAndPost(userId, newLike.postId).flatMap{
          case Some(_) => Future.successful(false, "You already liked this post")
          case None => likeRepository.addLike(userId, newLike.postId).map(addedSuccess => (addedSuccess, ""))
        }
      case None => Future.successful(false, "Post doesn't exist")
    }
  }

}
