package com.aiarchitect.service.ai;

import com.aiarchitect.exception.AiServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ArchitectureGeneratorStrategy} utilizing the Google Gemini 2.0 Flash API.
 *
 * This class:
 * 1. Constructs the system-engineered prompt via {@link PromptBuilder}.
 * 2. Prepares the API request body with instructions to return JSON.
 * 3. Triggers a synchronous POST request using the preconfigured `WebClient` bean.
 * 4. Navigates the response payload structure to retrieve the nested JSON string.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiArchitectureGenerator implements ArchitectureGeneratorStrategy {

    private final WebClient geminiWebClient;
    private final String geminiApiKey;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Calls Gemini to generate a software architecture blueprint.
     *
     * @param projectIdea The user's project description.
     * @return JSON string representing the populated 19 sections of the architecture report.
     * @throws AiServiceException if the request fails, times out, or the response cannot be parsed.
     */
    @Override
    public String generateArchitecture(String projectIdea) {
        log.info("Generating software architecture for idea: {}", projectIdea);

        try {
            // 1. Build prompt
            String prompt = promptBuilder.buildPrompt(projectIdea);

            // 2. Prepare Gemini request payload (forces JSON output via generationConfig)
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "responseMimeType", "application/json"
                    )
            );

            // 3. Invoke Gemini API using WebClient
            log.debug("Sending POST request to Gemini API...");
            String rawResponse = geminiWebClient.post()
                    .uri(":generateContent?key=" + geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (rawResponse == null) {
                throw new AiServiceException("Received empty response body from Gemini API");
            }

            // 4. Parse response node navigation: candidates[0].content.parts[0].text
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            String generatedJson = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (generatedJson == null || generatedJson.isBlank()) {
                throw new AiServiceException("Failed to extract generated JSON text content from Gemini response candidates");
            }

            log.info("Successfully received generated JSON report content from Gemini.");
            return generatedJson;

        } catch (Exception e) {
            log.error("Gemini API invocation failed: {}", e.getMessage(), e);
            throw new AiServiceException("AI service error: " + e.getMessage(), e);
        }
    }
}
