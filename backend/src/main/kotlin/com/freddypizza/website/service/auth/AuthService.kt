package com.freddypizza.website.service.auth

import com.freddypizza.website.config.JwtProperties
import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.exception.InvalidRefreshTokenException
import com.freddypizza.website.request.auth.AuthRequest
import com.freddypizza.website.response.auth.AuthResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
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

    fun refreshAccessToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): AuthResponse {
        val refreshToken =
            request.cookies?.firstOrNull { it.name == "refresh_token" }?.value
                ?: throw InvalidRefreshTokenException()

        if (tokenService.isExpired(refreshToken)) throw InvalidRefreshTokenException()

        val username = tokenService.extractUsername(refreshToken) ?: throw InvalidRefreshTokenException()
        val userDetails = userDetailsService.loadUserByUsername(username)

        val newAccessToken = getAccessToken(userDetails)
        val newRefreshToken = getRefreshToken(userDetails)

        response.addCookie(createCookie("access_token", newAccessToken, jwtProperties.accessTokenExpiration.toInt()))
        response.addCookie(createCookie("refresh_token", newRefreshToken, jwtProperties.refreshTokenExpiration.toInt()))

        return AuthResponse(newAccessToken, newRefreshToken)
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
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.maxAge = maxAge
        cookie.path = "/"
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
