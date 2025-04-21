package com.freddypizza.website.controller

import com.freddypizza.website.request.AuthRequest
import com.freddypizza.website.request.RefreshTokenRequest
import com.freddypizza.website.response.AuthResponse
import com.freddypizza.website.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping
    fun login(
        @RequestBody request: AuthRequest,
        response: HttpServletResponse,
    ): ResponseEntity<AuthResponse> {
        val authResponse = authService.authentication(request, response)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    fun refreshTokens(
        @RequestBody refreshRequest: RefreshTokenRequest,
        response: HttpServletResponse,
    ): ResponseEntity<AuthResponse> {
        val authResponse = authService.refreshAccessToken(refreshRequest.refreshToken)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        authService.logout(response)
        return ResponseEntity.ok("Logged out successfully")
    }
}
