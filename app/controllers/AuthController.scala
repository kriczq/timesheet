package controllers

import auth.JwtAuthentication
import javax.inject.Inject
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, Request, Result}
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}

case class UserCredentials(email: String, password: String)

object UserCredentials {

}

class AuthController @Inject()(cc: ControllerComponents, authService: AuthService, jwtAuth: JwtAuthentication)
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

  def signIn() = jwtAuth { implicit request =>
    Ok(Json.toJson(Map("message" -> ("Hi" + request.user.name))))
  }

  def login() = Action.async { implicit request =>
    processLogin()
  }

  private def processLogin[A]()(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[UserCredentials]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserCredentials) = {
      authService.validate(input).map(ok => Ok(ok.toString))
    }

    credentialsForm.bindFromRequest().fold(failure, success)
  }
}
