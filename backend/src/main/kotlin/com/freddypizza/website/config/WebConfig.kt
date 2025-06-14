package com.freddypizza.website.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${frontend.url}")
    private lateinit var allowedOriginsVar: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uploadPath =
            Paths
                .get("uploads/products")
                .toAbsolutePath()
                .toUri()
                .toString()
        registry
            .addResourceHandler("/uploads/products/**")
            .addResourceLocations(uploadPath)
    }
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(allowedOriginsVar)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
