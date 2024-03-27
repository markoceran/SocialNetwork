package helper

import play.api.libs.json.Json
import play.api.mvc.Results.Unauthorized
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TokenValidationAction @Inject()(parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends ActionBuilder[Request, AnyContent] {

  override def parser: BodyParser[AnyContent] = parser

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val isValid = TokenUtils.validateJwtToken(request)
    if (isValid) {
      block(request)
    } else {
      Future.successful(Unauthorized(Json.toJson("Invalid token")))
    }
  }
}
