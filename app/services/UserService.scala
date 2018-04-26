package handlers

import controllers.UserFormInput
import javax.inject.Inject
import models.{User, UserDao}
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

case class UserResource(id: Long, email: String, name: String)

object UserResource {

  implicit val userWrites: Writes[UserResource] = Json.writes[UserResource]
}

class UserHandler @Inject()(userDao: UserDao)(implicit ec: ExecutionContext) {

  def create(userFormInput: UserFormInput): Future[UserResource] = {
    val user = User(0, userFormInput.email, userFormInput.name, userFormInput.password)
    userDao.add(user).map { u => createUserResource(u) }
  }

  def list(): Future[Seq[UserResource]] = {
    userDao.all().map {
      userList => userList.map(u => createUserResource(u))
    }
  }

  private def createUserResource(user: User): UserResource = {
    UserResource(user.id, user.email, user.name)
  }
}
