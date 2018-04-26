package repositories

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long,
                email: String,
                name: String,
                password: String)

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def name = column[String]("name")
    def password = column[String]("password")

    override def * = (id, email, name, password) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UserTable]

  def add(user: User): Future[User] = {
    val insertQuery = users returning users.map(_.id) into ((item, id) => item.copy(id = id))
    val action = insertQuery += user
    db.run(action)
  }

  def all(): Future[Seq[User]] = {
    db.run(users.result)
  }

  def find(id: Long): Future[Option[User]] = {
    val action = users.filter(_.id === id).result.headOption
    db.run(action)
  }

}
