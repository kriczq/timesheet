package controllers

import javax.inject.Inject
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(email: String, name: String, password: String)

class UserController @Inject()(userService: UserService, cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val userForm: Form[UserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "email" -> email,
        "name" -> text,
        "password" -> text
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  def insert() = Action.async { implicit request =>
    processInserting()
  }

  def list() = Action.async { implicit request =>
    userService.list().map(list => Ok(Json.toJson(list)))
  }

  def get(id: Long) = Action.async { implicit request =>
    userService.find(id).map {
      case None => NotFound
      case Some(user) => Ok(Json.toJson(user))
    }
  }

  private def processInserting[A]()(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserFormInput) = {
      userService.create(input).map(output =>
        Created(Json.toJson(output)))
    }

    userForm.bindFromRequest().fold(failure, success)
  }
}
