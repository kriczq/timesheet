package daos


import com.google.inject.ImplementedBy
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long,
                email: String,
                name: String,
                password: String)

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {
  def add(user: User): Future[User]

  def all(): Future[Seq[User]]

  def find(id: Long): Future[Option[User]]

  def delete(id: Long): Future[Unit]

  def find(email: String): Future[Option[User]]
}

class UserDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserDao with HasDatabaseConfigProvider[JdbcProfile] {

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

  def delete(id: Long): Future[Unit] = {
    val action = users.filter(_.id === id).delete
    db.run(action).map(_ => ())
  }

  def find(email: String): Future[Option[User]] = {
    val action = users.filter(_.email === email).result.headOption
    db.run(action)
  }
}
