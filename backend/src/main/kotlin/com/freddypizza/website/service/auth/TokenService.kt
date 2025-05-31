package com.freddypizza.website.service.auth

import com.freddypizza.website.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    jwtProperties: JwtProperties,
) {
    private val logger = LoggerFactory.getLogger(TokenService::class.java)
    private val secretKey =
        Keys.hmacShaKeyFor(
            jwtProperties.key.toByteArray(),
        )

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap(),
    ): String =
        Jwts
            .builder()
            .claims(additionalClaims)
            .subject(userDetails.username)
            .claim("authorities", userDetails.authorities)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .signWith(secretKey)
            .compact()

    fun extractUsername(token: String): String? = getAllClaims(token)?.subject

    fun isExpired(token: String): Boolean =
        getAllClaims(token)?.expiration?.before(Date(System.currentTimeMillis()))
            ?: true

    fun isValid(
        token: String,
        userDetails: UserDetails,
    ): Boolean {
        val username = extractUsername(token)
        return userDetails.username == username && !isExpired(token)
    }

    private fun getAllClaims(token: String): Claims? =
        try {
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: JwtException) {
            logger.warn("Token validation error: ${ex.message}")
            null
        }
}
