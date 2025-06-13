package com.freddypizza.website.controller.auth

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.exception.ErrorResponse
import com.freddypizza.website.request.auth.AuthRequest
import com.freddypizza.website.response.auth.AuthResponse
import com.freddypizza.website.service.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping
    @Operation(summary = "Войти в систему")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешный вход"),
            ApiResponse(
                responseCode = "401",
                description = "Неверные учетные данные",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun login(
        @RequestBody request: AuthRequest,
        response: HttpServletResponse,
    ): ResponseEntity<AuthResponse> {
        val authResponse = authService.authentication(request, response)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токен доступа")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Токен обновлен успешно", content = []),
            ApiResponse(
                responseCode = "403",
                description = "Неверный токен обновления",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): AuthResponse = authService.refreshAccessToken(request, response)

    @PostMapping("/logout")
    @Operation(summary = "Выйти из системы")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Выход из системы выполнен успешно", content = [])],
    )
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        authService.logout(response)
        return ResponseEntity.ok("Logged out successfully")
    }

    @GetMapping("/me")
    fun whoAmI(
        @AuthenticationPrincipal user: CustomStaffUserDetails,
    ): Map<String, Any> =
        mapOf(
            "id" to user.id,
            "username" to user.username,
            "role" to user.getRole().name,
        )
}
