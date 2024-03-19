package services

import javax.inject.Inject
import models.User
import repositories.UserRepository

class UserService @Inject()(userRepository: UserRepository) {
  def createUser(user: User): Boolean = {
    if (isValidUser(user)) {
      userRepository.createUser(user)
    } else {
      false
    }
  }

  private def isValidUser(user: User): Boolean = {
    user.name.nonEmpty && user.lastName.nonEmpty && user.username.nonEmpty && user.email.nonEmpty && user.password.nonEmpty
  }
}
