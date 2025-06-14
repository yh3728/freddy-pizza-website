package com.freddypizza.website.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.freddypizza.website.exception.ErrorResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Value("\${frontend.url}")
    private lateinit var allowedOriginsVar: String
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): DefaultSecurityFilterChain =
        http
            .cors {}
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/admin/staff/**", "/admin/menu**")
                    .hasRole("ADMIN")
                    .requestMatchers("/admin/orders/**", "/admin/menu/**", "/admin/auth/me")
                    .fullyAuthenticated()
                    .requestMatchers("/admin/auth/logout", "/admin/auth/", "/admin/auth/refresh")
                    .permitAll()
                    .anyRequest()
                    .permitAll()
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

    @Bean
    fun corsFilter(): CorsFilter {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf(allowedOriginsVar)
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                allowedHeaders = listOf("*")
                allowCredentials = true
            }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return CorsFilter(source)
    }
}
