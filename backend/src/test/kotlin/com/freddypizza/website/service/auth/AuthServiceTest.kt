package com.freddypizza.website.service.auth

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.exception.InvalidRefreshTokenException
import com.freddypizza.website.repository.StaffRepository
import com.freddypizza.website.request.AuthRequest
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthServiceTest
    @Autowired
    constructor(
        private val underTest: AuthService,
        private val tokenService: TokenService,
        private val staffRepository: StaffRepository,
        private val encoder: BCryptPasswordEncoder,
    ) {
        private val validUsername = "testuser"
        private val validPassword = "password"
        private val invalidPassword = "wrongpassword"

        private val authRequest = AuthRequest(validUsername, validPassword)
        private val invalidAuthRequest = AuthRequest(validUsername, invalidPassword)

        private lateinit var staff: StaffEntity

        private lateinit var validRefreshToken: String
        private lateinit var expiredRefreshToken: String

        @BeforeEach
        fun setUp() {
            staff =
                StaffEntity(
                    username = validUsername,
                    password = encoder.encode(validPassword),
                    role = StaffRole.ADMIN,
                )
            staffRepository.save(staff)

            validRefreshToken =
                tokenService.generate(
                    userDetails = CustomStaffUserDetails(staff),
                    expirationDate = Date(System.currentTimeMillis() + 100000),
                )

            expiredRefreshToken =
                tokenService.generate(
                    userDetails = CustomStaffUserDetails(staff),
                    expirationDate = Date(System.currentTimeMillis() - 3600000),
                )
        }

        /**
         * Тест проверяет успешную аутентификацию с корректными данными.
         * Ожидается, что сервер вернет accessToken и refreshToken и установит соответствующие куки.
         */
        @Test
        fun `should authenticate and return tokens when credentials are valid`() {
            val response = MockHttpServletResponse()

            val result = underTest.authentication(authRequest, response)

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isNotBlank()

            val accessTokenCookie = response.cookies.find { it.name == "access_token" }
            val refreshTokenCookie = response.cookies.find { it.name == "refresh_token" }

            assertThat(accessTokenCookie).isNotNull
            assertThat(refreshTokenCookie).isNotNull

            assertThat(accessTokenCookie!!.isHttpOnly).isTrue
            assertThat(refreshTokenCookie!!.isHttpOnly).isTrue
        }

        /**
         * Тест проверяет, что при неправильном пароле выбрасывается BadCredentialsException.
         */
        @Test
        fun `should throw BadCredentialsException when credentials are invalid`() {
            val response = MockHttpServletResponse()

            assertThrows<BadCredentialsException> {
                underTest.authentication(invalidAuthRequest, response)
            }
        }

        /**
         * Тест проверяет успешное обновление accessToken через валидный refreshToken.
         */
        @Test
        fun `should refresh access token successfully`() {
            val result = underTest.refreshAccessToken(validRefreshToken)

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isEqualTo(validRefreshToken)
        }

        /**
         * Тест проверяет случай, когда передан просроченный refresh-токен.
         * Ожидается выбрасывание InvalidRefreshTokenException.
         */
        @Test
        fun `should throw InvalidRefreshTokenException when refresh token is expired`() {
            assertThrows<InvalidRefreshTokenException> {
                underTest.refreshAccessToken(expiredRefreshToken)
            }
        }
    }
