package com.aiarchitect.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI documentation configuration.
 *
 * WHY THIS EXISTS:
 * Swagger auto-generates interactive API documentation from our controllers.
 * Once the app is running, visit http://localhost:8080/swagger-ui.html to see
 * every endpoint, try them out, and see request/response formats.
 *
 * This is critical for:
 * 1. Frontend developers who need to know the API contract
 * 2. Recruiters and reviewers who want to test the API without code
 * 3. Your own debugging during development
 *
 * The security scheme configuration tells Swagger UI to include a
 * "Authorize" button where you can paste your JWT token. Without this,
 * you couldn't test protected endpoints from the Swagger UI.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Software Architect API")
                        .version("1.0.0")
                        .description("""
                                AI-powered software architecture generator.
                                Submit a project idea in plain English and receive a complete,
                                professional-grade software architecture including database design,
                                API endpoints, Spring Boot structure, and deployment strategy.
                                """)
                        .contact(new Contact()
                                .name("AI Software Architect")
                                .email("contact@aiarchitect.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Add JWT as a global security requirement
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without the 'Bearer ' prefix)")));
    }
}
