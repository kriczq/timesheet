package services

import controllers.UserFormInput
import javax.inject.Inject
import play.api.libs.json.{Json, Writes}
import repositories.{User, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

case class UserResource(id: Long, email: String, name: String)

object UserResource {

  implicit val userWrites: Writes[UserResource] = Json.writes[UserResource]
}

class UserService @Inject()(userRepo: UserRepository)(implicit ec: ExecutionContext) {

  def create(userFormInput: UserFormInput): Future[UserResource] = {
    val user = User(0, userFormInput.email, userFormInput.name, userFormInput.password)
    userRepo.add(user).map { u => createUserResource(u) }
  }

  def list(): Future[Seq[UserResource]] = {
    userRepo.all().map {
      userList => userList.map(u => createUserResource(u))
    }
  }

  def find(id: Long): Future[Option[UserResource]] = {
    userRepo.find(id).map { maybeUser =>
      maybeUser.map { user =>
        createUserResource(user)
      }
    }
  }

  private def createUserResource(user: User): UserResource = {
    UserResource(user.id, user.email, user.name)
  }
}
