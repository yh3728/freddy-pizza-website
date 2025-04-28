package com.freddypizza.website.service.auth

import com.freddypizza.website.detail.CustomStaffUserDetails
import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class TokenServiceTest
    @Autowired
    constructor(
        private val tokenService: TokenService,
    ) {
        private val validUsername = "testUser"
        private val userDetails =
            CustomStaffUserDetails(StaffEntity(username = validUsername, password = "password", role = StaffRole.ADMIN))

        private val expiredToken: String =
            tokenService.generate(
                userDetails = userDetails,
                expirationDate = Date(System.currentTimeMillis() - 1000),
            )

        private val validToken: String =
            tokenService.generate(
                userDetails = userDetails,
                expirationDate = Date(System.currentTimeMillis() + 100000),
            )

        private val invalidToken = "invalid.token.value"

        /**
         * Тест проверяет, что генерируемый токен является валидным.
         * Ожидается, что метод вернет непустой и валидный токен.
         */
        @Test
        fun `should generate valid token`() {
            assertThat(validToken).isNotBlank()
            assertThat(tokenService.isValid(validToken, userDetails)).isTrue()
        }

        /**
         * Тест проверяет, что из валидного токена можно корректно извлечь имя пользователя.
         * Ожидается, что метод вернет правильное имя пользователя.
         */
        @Test
        fun `should extract username from token`() {
            val extractedUsername = tokenService.extractUsername(validToken)
            assertThat(extractedUsername).isEqualTo(validUsername)
        }

        /**
         * Тест проверяет, что метод возвращает true для валидного токена.
         */
        @Test
        fun `should return true for valid token`() {
            val isValid = tokenService.isValid(validToken, userDetails)
            assertThat(isValid).isTrue()
        }

        /**
         * Тест проверяет, что метод isValid возвращает false для невалидного токена.
         */
        @Test
        fun `should return false for invalid token`() {
            val isValid = tokenService.isValid(invalidToken, userDetails)
            assertThat(isValid).isFalse()
        }

        /**
         * Тест проверяет, что метод возвращает true для истекшего токена.
         */
        @Test
        fun `should return false for expired token`() {
            val isExpired = tokenService.isExpired(expiredToken)
            assertThat(isExpired).isTrue()
        }

        /**
         * Тест проверяет, что метод не сможет извлечь имя пользователя из невалидного токена.
         * Ожидается, что метод возвращает null.
         */
        @Test
        fun `should return null for extracting username from invalid token`() {
            val username = tokenService.extractUsername(invalidToken)
            assertThat(username).isNull()
        }
    }
