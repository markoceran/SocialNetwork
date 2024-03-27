package services

import models.{NewPost, Post, UpdatePost}
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

  def getPostById(id: BigInt): Future[Option[Post]] = {
    postRepository.getPostById(id).map {
      case Some(post) =>
        Some(post)
      case None => None
    }
  }
  def editPost(userId: BigInt, updatePost: UpdatePost): Future[(Boolean, String)] = {
    postRepository.getPostById(updatePost.postId).flatMap {
      case Some(post) =>
        if(post.postedBy.id == userId){
          if (updatePost.newContent.length <= 250 && updatePost.newContent != "" && updatePost.newContent.length > 0) {
            postRepository.editPost(updatePost.postId, updatePost.newContent).map(created => (created, ""))
          } else {
            Future.successful(false, "Post must contain more than 0 characters and no more that 250 characters")
          }
        }else {
          Future.successful(false, "Post doesn't belong to the logged in user")
        }
      case None => Future.successful(false, "Post doesn't exist")
    }
  }


}
