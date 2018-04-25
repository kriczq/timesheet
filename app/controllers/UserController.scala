package controllers

import javax.inject.Inject
import models.{User, UserDao}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class UserController @Inject()(userDao: UserDao,
                                cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc){


  def insert = Action.async { implicit request: Request[AnyContent] =>
    val user = User("dawid", "asdasd", "haslo")
    userDao.add(user).map(u => Ok(u.toString))
  }

  def list = Action.async { implicit request: Request[AnyContent] =>
    userDao.all().map(u => Ok(u.toString()))
  }
}
