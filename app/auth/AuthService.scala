package auth

import daos.UserDao
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class AuthService @Inject()(userDao: UserDao)(implicit executionContext: ExecutionContext) {
  def validate(userCredentials: UserCredentials): Future[Boolean] = {
    val maybeUser = userDao.find(userCredentials.email)
    maybeUser.map { maybeUser =>
      maybeUser.fold(false)(user => user.password == userCredentials.password)
    }
  }
}
