package helper

import io.jsonwebtoken.security.Keys

import java.util.Date
import io.jsonwebtoken.{JwtBuilder, Jwts, SignatureAlgorithm}
import play.api.mvc.{AnyContent, Request}


object TokenUtils {

  private val jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

  def generateJwtToken(username: String, id: BigInt): String = {
    val nowMillis = System.currentTimeMillis()
    val expMillis = nowMillis + 3600000 // Token expires in 1 hour
    val now = new Date(nowMillis)
    val exp = new Date(expMillis)

    val jwtBuilder: JwtBuilder = Jwts.builder()
      .setIssuedAt(now)
      .setExpiration(exp)
      .claim("username", username)
      .claim("id", id.toString)
      .signWith(SignatureAlgorithm.HS256, jwtSecretKey)

    jwtBuilder.compact()
  }

  def validateJwtToken(request: Request[Any]): Boolean = {
    try {
      val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
      val claims = Jwts.parser()
        .setSigningKey(jwtSecretKey)
        .parseClaimsJws(token)
        .getBody

      val expiration = claims.getExpiration
      val now = new Date()
      expiration.after(now)
    } catch {
      case _: Exception =>
        false
    }
  }

  def getUsernameFromToken(request: Request[Any]): Option[String] = {
    try {
      val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
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

  def getUserIdFromToken(request: Request[Any]): Option[BigInt] = {
    try {
      val token = request.headers.get("Authorization").map(_.replace("Bearer ", "")).getOrElse("")
      val claims = Jwts.parser()
        .setSigningKey(jwtSecretKey)
        .parseClaimsJws(token)
        .getBody

      val id = claims.get("id", classOf[String])
      Some(BigInt(id))
    } catch {
      case _: Exception =>
        None
    }
  }

}