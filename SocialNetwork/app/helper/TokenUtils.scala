package helper

import io.jsonwebtoken.security.Keys

import java.util.Date
import io.jsonwebtoken.{JwtBuilder, Jwts, SignatureAlgorithm}

object TokenUtils {

  private val jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

  def generateJwtToken(username: String): String = {
    val nowMillis = System.currentTimeMillis()
    val expMillis = nowMillis + 3600000 // Token expires in 1 hour
    val now = new Date(nowMillis)
    val exp = new Date(expMillis)

    val jwtBuilder: JwtBuilder = Jwts.builder()
      .setIssuedAt(now)
      .setExpiration(exp)
      .claim("username", username)
      .signWith(SignatureAlgorithm.HS256, jwtSecretKey)

    jwtBuilder.compact()
  }

  def validateJwtToken(token: String): (Boolean, Boolean) = {
    try {
      val claims = Jwts.parser()
        .setSigningKey(jwtSecretKey)
        .parseClaimsJws(token)
        .getBody

      val username = claims.get("username", classOf[String])
      val expiration = claims.getExpiration

      val isValid = true
      val isExpired = expiration.before(new Date())

      (isValid, isExpired)
    } catch {
      case _: Exception =>
        val isValid = false
        val isExpired = false
        (isValid, isExpired)
    }
  }

  def getUsernameFromToken(token: String): Option[String] = {
    try {
      val claims = Jwts.parser()
        .setSigningKey(jwtSecretKey)
        .parseClaimsJws(token)
        .getBody

      val username = claims.get("username", classOf[String])
      Some(username)
    } catch {
      case _: Exception =>
        None
    }
  }

}