package services

import javax.inject.Inject
import models.User
import repositories.UserRepository

class UserService @Inject()(userRepository: UserRepository) {
  def addUser(user: User): Boolean = {
    if (isValidUser(user)) {
      userRepository.addUser(user)
    } else {
      false
    }
  }

  private def isValidUser(user: User): Boolean = {
    user.name.nonEmpty && user.lastName.nonEmpty && user.username.nonEmpty && user.email.nonEmpty && user.password.nonEmpty
  }
}
