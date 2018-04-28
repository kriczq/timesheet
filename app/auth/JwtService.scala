package auth

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

class JwtService {
  private val JwtSecretKey = "JwtSecretKey"
  private val JwtAlgo = "HS256"

  def createToken(payload: String): String = {
    val header = JwtHeader(JwtAlgo)
    val claimsSet = JwtClaimsSet(payload)
    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _                                          => None
    }
}

object JwtService extends JwtService