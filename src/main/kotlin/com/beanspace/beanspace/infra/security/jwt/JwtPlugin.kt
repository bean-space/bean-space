package com.beanspace.beanspace.infra.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtPlugin(
    @Value("BeanSpace") private val issuer: String,
    @Value("PO5o6c72FN672Fd31967VWbAWq4Ws5aZ") private val secret: String,
    @Value("100000") private val accessTokenExpirationHour: Long
) {
    var now = Date()
    val accessTokenExpiration = Date(now.time + accessTokenExpirationHour * 1000 * 1000)
    val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)
        }
    }

    fun generateAccessToken(
        subject: String,
        username: String,
        role: String
    ): String {
        return generateToken(subject, username, role, accessTokenExpiration)
    }

    private fun generateToken(
        subject: String,
        username: String,
        role: String,
        accessTokenExpiration: Date
    ): String {
        val claims: Claims = Jwts.claims()
            .add(mapOf("role" to role, "username" to username))
            .build()

        return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .issuedAt(now)
            .expiration(accessTokenExpiration)
            .claims(claims)
            .signWith(key)
            .compact()
    }
}