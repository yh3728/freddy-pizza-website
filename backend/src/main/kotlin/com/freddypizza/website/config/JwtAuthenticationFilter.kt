package com.freddypizza.website.config

import com.freddypizza.website.service.StaffUserDetailsService
import com.freddypizza.website.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val staffUserDetailsService: StaffUserDetailsService,
    private val tokenService: TokenService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessTokenCookie = getCookie(request, "access_token")
        if (accessTokenCookie != null) {
            val jwtToken = accessTokenCookie.value
            val login = tokenService.extractUsername(jwtToken)
            if (login != null && SecurityContextHolder.getContext().authentication == null) {
                val foundUser = staffUserDetailsService.loadUserByUsername(login)
                if (tokenService.isValid(jwtToken, foundUser)) {
                    updateContext(foundUser, request)
                }
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun getCookie(
        request: HttpServletRequest,
        cookieName: String,
    ): Cookie? = request.cookies?.find { it.name == cookieName }

    private fun updateContext(
        foundUser: UserDetails,
        request: HttpServletRequest,
    ) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}
