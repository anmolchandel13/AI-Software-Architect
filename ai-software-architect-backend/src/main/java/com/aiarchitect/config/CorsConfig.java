package com.aiarchitect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Cross-Origin Resource Sharing (CORS) configuration.
 *
 * WHY THIS EXISTS:
 * Browsers enforce the "Same-Origin Policy" — a security rule that blocks
 * JavaScript from making HTTP requests to a different domain/port.
 *
 * Our React frontend runs on http://localhost:3000
 * Our Spring Boot backend runs on http://localhost:8080
 *
 * Without CORS configuration, the browser would BLOCK every API call
 * from React to Spring Boot. This class tells Spring: "Allow requests
 * from these specific origins."
 *
 * SECURITY NOTE: In production, replace the wildcard with your actual
 * frontend domain (e.g., "https://aisoftwarearchitect.com").
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Which origins (frontend URLs) are allowed to call our API
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",    // React dev server
                "http://localhost:5173"     // Vite dev server (alternative)
        ));

        // Which HTTP methods are allowed
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Which HTTP headers the frontend can send
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",   // For JWT tokens
                "Content-Type",    // For JSON bodies
                "Accept"           // For content negotiation
        ));

        // Whether to include cookies/auth headers in cross-origin requests
        config.setAllowCredentials(true);

        // How long the browser caches the CORS preflight response (1 hour)
        config.setMaxAge(3600L);

        // Apply this CORS config to ALL endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
