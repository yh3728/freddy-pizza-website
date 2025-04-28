package com.freddypizza.website.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.freddypizza.website.exception.ErrorResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/admin/auth", "/admin/auth/refresh", "/admin/auth/logout", "/error")
                    .permitAll()
                    .requestMatchers("/admin/staff/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .fullyAuthenticated()
            }.exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.characterEncoding = "UTF-8"
                    val errorResponse =
                        ErrorResponse(
                            error = HttpStatus.UNAUTHORIZED.name,
                            message = "Для доступа требуется авторизация",
                        )
                    response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
                }
                it.accessDeniedHandler { _, response, _ ->
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    response.characterEncoding = "UTF-8"
                    val errorResponse =
                        ErrorResponse(
                            error = HttpStatus.FORBIDDEN.name,
                            message = "Доступ запрещён",
                        )
                    response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
                }
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}
