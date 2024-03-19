package models

import play.api.libs.json._


class User(val name: String, val lastName: String, val username: String, val email:String, val password: String)

object User {
  // Define an apply method in the companion object
  def apply(name: String, lastName: String, username: String, email: String, password: String): User = {
    new User(name, lastName, username, email, password)
  }

  // Define an unapply method in the companion object
  def unapply(arg: User): Option[(String, String, String, String, String)] = {
    Some((arg.name, arg.lastName, arg.username, arg.email, arg.password))
  }

  // Define JSON Reads for the User class
  implicit val userReads: Reads[User] = Json.reads[User]
}