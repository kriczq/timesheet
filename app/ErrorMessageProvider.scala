package auth

import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results._

object ErrorMessageProvider {

  def apply(status: Status, message: String): Result = {
    val json = Json.toJson(Map("message" -> message))
    status(json)
  }

  def errorInvalidCredentials = apply(Unauthorized, "Invalid credentials")

  def errorInvalidToken = apply(Unauthorized, "Invalid token")

  def errorUserNotFound = apply(Unauthorized, "User not found")

  def errorUserAlreadyExists = apply(Conflict, "User already exists")

  def errorTokenHeaderNotFound = apply(Unauthorized, "Auth Token header not found")
}