package services

import controllers.UserFormInput
import daos.{User, UserDao}
import javax.inject.Inject
import play.api.libs.json.{Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

case class UserResource(id: Long, email: String, name: String)

object UserResource {

  implicit val userWrites: Writes[UserResource] = Json.writes[UserResource]
}

class UserService @Inject()(userDao: UserDao)(implicit ec: ExecutionContext) {
  def remove(id: Long): Future[Unit] = {
    userDao.delete(id)
  }

  def create(userFormInput: UserFormInput): Future[UserResource] = {
    val user = User(0, userFormInput.email, userFormInput.name, userFormInput.password)
    userDao.add(user).map { u => createUserResource(u) }
  }

  def list(): Future[Seq[UserResource]] = {
    userDao.all().map {
      userList => userList.map(u => createUserResource(u))
    }
  }

  def find(id: Long): Future[Option[UserResource]] = {
    userDao.find(id).map { maybeUser =>
      maybeUser.map { user =>
        createUserResource(user)
      }
    }
  }

  private def createUserResource(user: User): UserResource = {
    UserResource(user.id, user.email, user.name)
  }
}
