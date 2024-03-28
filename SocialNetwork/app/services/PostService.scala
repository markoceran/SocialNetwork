package services

import models.{DeletePost, NewPost, Post, UpdatePost}
import repositories.PostRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostService @Inject()(postRepository: PostRepository) (implicit ec: ExecutionContext){

  def createPost(postedById: BigInt, newPost: NewPost): Future[(Boolean, String)] = {
    if (validatePostContent(newPost.content)) {
      postRepository.createPost(postedById, newPost.content).map(createdSuccess => (createdSuccess, ""))
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
          if (validatePostContent(updatePost.newContent)) {
            postRepository.editPost(updatePost.postId, updatePost.newContent).map(editedSuccess => (editedSuccess, ""))
          } else {
            Future.successful(false, "Post must contain more than 0 characters and no more that 250 characters")
          }
        }else {
          Future.successful(false, "Post doesn't belong to the logged in user")
        }
      case None => Future.successful(false, "Post doesn't exist")
    }
  }

  private def validatePostContent(content: String): Boolean = {
    if (content.length <= 250 && content != "" && content.length > 0) {
      true
    } else {
      false
    }
  }

  def deletePost(userId: BigInt, deletePost: DeletePost): Future[(Boolean, String)] = {
    postRepository.getPostById(deletePost.postId).flatMap {
      case Some(post) =>
        if (post.postedBy.id == userId) {
          postRepository.deletePost(deletePost.postId).map(deletedSuccess => (deletedSuccess, ""))
        } else {
          Future.successful(false, "Post doesn't belong to the logged in user")
        }
      case None => Future.successful(false, "Post doesn't exist")
    }
  }

}
