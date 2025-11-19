package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(user: User): String =
        Jwts.builder()
            .setSubject(user.id.toString())
            .claim("username", user.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 15 * 60 * 1000))
            .signWith(key)
            .compact()
}
