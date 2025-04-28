package com.freddypizza.website.service.auth

import com.freddypizza.website.config.JwtProperties
import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.exception.InvalidRefreshTokenException
import com.freddypizza.website.request.AuthRequest
import com.freddypizza.website.response.AuthResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
) {
    fun authentication(
        authRequest: AuthRequest,
        response: HttpServletResponse,
    ): AuthResponse {
        val authentication =
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.username,
                    authRequest.password,
                ),
            )
        val user = authentication.principal as CustomStaffUserDetails
        val accessToken = getAccessToken(user)
        val refreshToken = getRefreshToken(user)

        val accessTokenCookie = createCookie("access_token", accessToken, jwtProperties.accessTokenExpiration.toInt())
        response.addCookie(accessTokenCookie)

        val refreshTokenCookie = createCookie("refresh_token", refreshToken, jwtProperties.refreshTokenExpiration.toInt())
        response.addCookie(refreshTokenCookie)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun refreshAccessToken(token: String): AuthResponse {
        if (tokenService.isExpired(token)) {
            throw InvalidRefreshTokenException()
        }

        val extractedUsername = tokenService.extractUsername(token) ?: throw InvalidRefreshTokenException()
        val currentUserDetails = userDetailsService.loadUserByUsername(extractedUsername)

        val accessToken = getAccessToken(currentUserDetails)
        return AuthResponse(accessToken, token)
    }

    private fun getRefreshToken(user: UserDetails) =
        tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration),
        )

    private fun getAccessToken(user: UserDetails) =
        tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )

    private fun createCookie(
        name: String,
        value: String,
        maxAge: Int,
    ): Cookie {
        val cookie = Cookie(name, value)
        cookie.isHttpOnly = true // Запрещает доступ через JavaScript
        cookie.secure = true // Отправляется только через HTTPS
        cookie.maxAge = maxAge // Срок действия cookie
        cookie.path = "/" // Доступна на всем сайте
        return cookie
    }

    fun logout(response: HttpServletResponse) {
        val accessTokenCookie =
            Cookie("access_token", null).apply {
                maxAge = 0
                path = "/"
            }
        val refreshTokenCookie =
            Cookie("refresh_token", null).apply {
                maxAge = 0
                path = "/"
            }
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)
    }
}
