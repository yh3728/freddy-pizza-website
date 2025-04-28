package com.freddypizza.website.controller.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freddypizza.website.exception.InvalidRefreshTokenException
import com.freddypizza.website.request.AuthRequest
import com.freddypizza.website.request.RefreshTokenRequest
import com.freddypizza.website.response.AuthResponse
import com.freddypizza.website.service.auth.AuthService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
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
        private val refreshTokenRequest = RefreshTokenRequest("refresh_token")
        private val expectedTokenResponse = AuthResponse("new_access_token", "new_refresh_token")

        private val invalidRefreshToken = RefreshTokenRequest("invalid_refresh_token")

        @BeforeEach
        fun setUp() {
            every { authService.authentication(authRequest, any()) } returns authResponse
            every { authService.refreshAccessToken("refresh_token") } returns expectedTokenResponse
            every { authService.refreshAccessToken("invalid_refresh_token") } throws InvalidRefreshTokenException()
            every { authService.logout(any()) } returns Unit
            every { authService.authentication(invalidAuthRequest, any()) } throws BadCredentialsException("Неверные учетные данные")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(refreshTokenRequest)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"))

            verify { authService.refreshAccessToken("refresh_token") }
        }

        /**
         * Тест проверяет ошибку при некорректном refresh token.
         */
        @Test
        fun `should return forbidden when refresh token is invalid`() {
            mockMvc
                .perform(
                    post("/admin/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper().writeValueAsString(invalidRefreshToken)),
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
    }
