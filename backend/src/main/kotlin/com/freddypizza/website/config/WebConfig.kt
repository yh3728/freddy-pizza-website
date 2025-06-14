package com.freddypizza.website.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig : WebMvcConfigurer {
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
}
