package com.aiarchitect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for the Google Gemini AI API client.
 *
 * WHY WebClient (not RestTemplate):
 * - RestTemplate is deprecated in Spring 6+.
 * - WebClient is the modern, non-blocking HTTP client recommended by Spring.
 * - Even though we're using it synchronously (with .block()), it's the correct
 *   choice for new projects.
 *
 * This class creates a pre-configured WebClient bean with the Gemini API
 * base URL already set. When our GeminiArchitectureGenerator needs to call
 * the API, it just injects this bean and adds the specific endpoint path.
 */
@Configuration
public class GeminiConfig {

    @Value("${app.gemini.api-url}")
    private String apiUrl;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    /**
     * Creates a WebClient pre-configured for Gemini API calls.
     * Base URL includes the model and API key so individual calls
     * only need to specify the action (e.g., ":generateContent").
     */
    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl + "/" + model)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))  // 10MB — AI responses can be large
                .build();
    }

    /**
     * Exposes the API key as a bean so it can be injected where needed
     * without passing it through multiple layers.
     */
    @Bean
    public String geminiApiKey() {
        return apiKey;
    }
}
