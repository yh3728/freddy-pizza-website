package com.freddypizza.website.controller.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidRefreshTokenException
import com.freddypizza.website.request.auth.AuthRequest
import com.freddypizza.website.response.auth.AuthResponse
import com.freddypizza.website.service.auth.AuthService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest
    @Autowired
    constructor(
        @MockkBean val authService: AuthService,
        private val mockMvc: MockMvc,
    ) {
        private val authRequest = AuthRequest("admin", "password")
        private val authResponse = AuthResponse("access_token", "refresh_token")
        private val invalidAuthRequest = AuthRequest("invalid_user", "wrong_password")
        private val expectedTokenResponse = AuthResponse("new_access_token", "new_refresh_token")

        @BeforeEach
        fun setUp() {
            every { authService.authentication(authRequest, any()) } returns authResponse
            every { authService.authentication(invalidAuthRequest, any()) } throws BadCredentialsException("Неверные учетные данные")
            every { authService.refreshAccessToken(any(), any()) } returns expectedTokenResponse
            every { authService.logout(any()) } returns Unit
        }

        /**
         * Тест проверяет успешный вход пользователя.
         * Отправляется POST-запрос на /admin/auth с данными пользователя.
         * Ожидается статус 200 OK и токены в ответе.
         */
        @Test
        fun `should authenticate admin and return tokens`() {
            mockMvc
                .perform(
                    post("/admin/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(authRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"))

            verify { authService.authentication(authRequest, any()) }
        }

        /**
         * Тест проверяет успешное обновление токенов через refresh_token.
         * Ожидается статус 200 OK и новые токены в ответе.
         */
        @Test
        fun `should refresh tokens`() {
            mockMvc
                .perform(
                    post("/admin/auth/refresh")
                        .cookie(Cookie("refreshToken", "refresh_token")),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"))

            verify { authService.refreshAccessToken(any(), any()) }
        }

        /**
         * Тест проверяет ошибку при некорректном refresh token.
         */
        @Test
        fun `should return forbidden when refresh token is invalid`() {
            every { authService.refreshAccessToken(any(), any()) } throws InvalidRefreshTokenException()
            mockMvc
                .perform(
                    post("/admin/auth/refresh")
                        .cookie(Cookie("refreshToken", "invalid_refresh_token")),
                ).andExpect(status().isForbidden)
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Неверный токен обновления"))
        }

        /**
         * Тест проверяет успешный выход пользователя.
         * Отправляется POST-запрос на /admin/auth/logout.
         * Ожидается статус 200 OK и сообщение об успешном выходе.
         */
        @Test
        @WithMockUser
        fun `should logout admin successfully`() {
            mockMvc
                .perform(
                    post("/admin/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(content().string("Logged out successfully"))

            verify { authService.logout(any()) }
        }

        /**
         * Тест проверяет, что ответ на аутентификацию содержит корректные заголовки.
         */
        @Test
        fun `should contain correct headers on authentication`() {
            mockMvc
                .perform(
                    post("/admin/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(authRequest)),
                ).andExpect(status().isOk)
                .andExpect(header().exists("Content-Type"))
        }

        /**
         * Тест проверяет ошибку при неправильных учетных данных.
         * Ожидается статус 401 (Unauthorized) и соответствующее сообщение об ошибке.
         */
        @Test
        fun `should return unauthorized when credentials are invalid`() {
            mockMvc
                .perform(
                    post("/admin/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(invalidAuthRequest)),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Неверные учетные данные"))

            verify { authService.authentication(invalidAuthRequest, any()) }
        }

        @Test
        fun `should return current user info on me endpoint`() {
            val adminStaff =
                CustomStaffUserDetails(
                    StaffEntity(1, "admin", "password", StaffRole.ADMIN),
                )
            mockMvc
                .get("/admin/auth/me") { with(user(adminStaff)) }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.username") { value("admin") }
                    jsonPath("$.role") { value("ADMIN") }
                }
        }
    }
