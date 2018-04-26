package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long,
                email: String,
                name: String,
                password: String)


class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def email = column[String]("email")
    def name = column[String]("name")
    def password = column[String]("password")

    override def * = (id, email, name, password) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UserTable]

  def add(user: User): Future[Int] = db.run(users += user)

  def all(): Future[Seq[User]] = db.run(users.result)
}
