package services

import javax.inject.Inject
import models.User
import repositories.UserRepository
import scala.util.matching.Regex

class UserService @Inject()(userRepository: UserRepository) {
  def createUser(user: User): Boolean = {
      userRepository.createUser(user)
  }

  def getUserByUsername(username: String): Option[User] = {
    userRepository.getUserByUsername(username)
  }

}
