package auth

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{Json, Reads}
import play.api.mvc.Results._
import play.api.mvc._
import services.{UserResource, UserService}

import scala.concurrent.{ExecutionContext, Future}

case class UserInfo(id: Long, email: String)

object UserInfo {
  implicit val userReads: Reads[UserInfo] = Json.reads[UserInfo]

}

class UserRequest[A](val user: UserResource, request: Request[A]) extends WrappedRequest[A](request)

class JwtAuthentication @Inject()(parsers: PlayBodyParsers, userService: UserService)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {
  override def parser: BodyParser[AnyContent] = parsers.anyContent

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    Logger.info("Calling action")

    val jwtToken = request.headers.get("X-Auth-Token").getOrElse("")

    if (JwtService.isValidToken(jwtToken)) {
      JwtService.decodePayload(jwtToken).fold {
        Future.successful(Unauthorized("Invalid credential"))
      } { payload =>
        val userCredentials = Json.parse(payload).validate[UserInfo].get
        val futureMaybeUser = userService.find(userCredentials.id)

        futureMaybeUser flatMap {
          case Some(user) => block(new UserRequest[A](user, request))
          case None => Future.successful(Unauthorized("Invalid credential"))
        }
      }
    } else {
      Future.successful(Unauthorized("Invalid credential"))
    }
  }
}
