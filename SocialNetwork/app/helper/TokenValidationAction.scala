package helper

import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class AuthenticatedRequest[A](userId: BigInt, username: String, request: Request[A]) extends WrappedRequest[A](request)

class TokenValidationAction @Inject()(parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    val isValid = TokenUtils.validateJwtToken(request)
    if (isValid) {
      val usernameOption = TokenUtils.getUsernameFromToken(request)
      val userIdOption = TokenUtils.getUserIdFromToken(request)
      (usernameOption, userIdOption) match {
        case (Some(username), Some(userId)) =>
          // Create AuthenticatedRequest and invoke block
          block(AuthenticatedRequest(userId, username, request))
        case _ =>
          Future.successful(Unauthorized)
      }
    } else {
      Future.successful(Unauthorized)
    }
  }
}

