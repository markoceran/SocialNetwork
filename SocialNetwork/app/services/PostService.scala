package services

import models.{NewPost}
import repositories.PostRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostService @Inject()(postRepository: PostRepository) (implicit ec: ExecutionContext){

  def createPost(postedById: BigInt, newPost: NewPost): Future[(Boolean, String)] = {
    if (newPost.content.length <= 250 && newPost.content != "" && newPost.content.length > 0) {
      postRepository.createPost(postedById, newPost.content).map(created => (created, ""))
    } else {
      Future.successful(false, "Post must contain more than 0 characters and no more that 250 characters")
    }
  }


}
