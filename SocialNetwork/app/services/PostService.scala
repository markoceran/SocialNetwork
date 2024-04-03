package services

import helper.PaginationHelper
import models.{DeletePost, GetUserPosts, NewPost, Pagination, Post, PostDetailsResponse, UpdatePost}
import repositories.{LikeRepository, PostRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostService @Inject()(postRepository: PostRepository, likeRepository: LikeRepository) (implicit ec: ExecutionContext){

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
    content.length <= 250 && content != "" && content.length > 0
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

  def getPostsByUser(loggedUserId: BigInt, getUserPosts: GetUserPosts): Future[List[PostDetailsResponse]] = {
    val pageNumber = getUserPosts.pagination.pageNumber.getOrElse(1)
    val pageSize = getUserPosts.pagination.pageSize.getOrElse(10)

    if (PaginationHelper.validatePagination(pageNumber, pageSize)) {
      postRepository.getPostsByUser(getUserPosts.username, pageNumber, pageSize).flatMap { postList =>
        extendPostWithDetails(postList, loggedUserId)
      }
    } else {
      Future.successful(List.empty[PostDetailsResponse])
    }
  }

  def getMyFriendsPosts(loggedUserId: BigInt, pagination: Pagination): Future[List[PostDetailsResponse]] = {
    val pageNumber = pagination.pageNumber.getOrElse(1)
    val pageSize = pagination.pageSize.getOrElse(10)

    if (PaginationHelper.validatePagination(pageNumber, pageSize)) {
      postRepository.getMyFriendsPosts(loggedUserId, pageNumber, pageSize).flatMap{ postList =>
        extendPostWithDetails(postList, loggedUserId)
      }
    } else {
      Future.successful(List.empty[PostDetailsResponse])
    }
  }

  private def extendPostWithDetails(postList: List[Post], loggedUserId: BigInt): Future[List[PostDetailsResponse]] = {
    Future.sequence {
      postList.map { basicPost =>
        for {
          likeCount <- likeRepository.likeCount(basicPost.id)
          liked <- likeRepository.getLikeByUserAndPost(loggedUserId, basicPost.id).map(_.isDefined)
        } yield PostDetailsResponse(basicPost.id, basicPost.content, basicPost.postedBy, basicPost.creationDate, basicPost.edited, likeCount, liked)
      }
    }
  }

}
