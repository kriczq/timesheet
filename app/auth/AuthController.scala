package auth

import java.util.Calendar

import auth.ErrorMessageProvider._
import javax.inject.Inject
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

case class UserCredentials(email: String, password: String)

case class UserClaims(userCredentials: UserCredentials, expirationDate: Long)

object UserCredentials {
  implicit val userWrites: Writes[UserCredentials] = Json.writes[UserCredentials]
  implicit val userReads: Reads[UserCredentials] = Json.reads[UserCredentials]
}

object UserClaims {
  implicit val userWrites: Writes[UserClaims] = Json.writes[UserClaims]
  implicit val userReads: Reads[UserClaims] = Json.reads[UserClaims]
}

class AuthController @Inject()(cc: ControllerComponents, jwtService: JwtService,
                               authService: AuthService, jwtAuth: JwtAuthentication)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val credentialsForm: Form[UserCredentials] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "email" -> email,
        "password" -> text
      )(UserCredentials.apply)(UserCredentials.unapply)
    )
  }

  def testAuth() = jwtAuth { implicit request =>
    Ok(Json.toJson(Map("message" -> s"Hi ${request.user.name}")))
  }

  def login() = Action.async { implicit request =>
    processLogin()
  }

  private def processLogin[A]()(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[UserCredentials]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(credentials: UserCredentials) = {
      authService.validate(credentials).map {
        case true =>
          val now = Calendar.getInstance()
          now.add(Calendar.DATE, 7)
          val expirationDate = now.getTimeInMillis
          val claims = Json.toJson(UserClaims(credentials, expirationDate)).toString()
          Ok(jwtService.createToken(claims))
        case false => errorInvalidCredentials
      }
    }

    credentialsForm.bindFromRequest().fold(failure, success)
  }
}
