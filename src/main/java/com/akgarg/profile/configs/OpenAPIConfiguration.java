package com.akgarg.profile.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        final var information = new Info()
                .title("Profile Management API for URL Shortener")
                .version("1.0.0")
                .description("This API provides endpoints for managing user profiles, including creating, retrieving, updating, and deleting user information. It is part of the URL shortener service.");
        return new OpenAPI().info(information);
    }

}
