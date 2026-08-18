package com.phly101.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI().info(new Info().title("Spring-Book Library API")
                .version("1.0")
                .description("REST API for managing books, members, and loans in a library system."));
    }
}
