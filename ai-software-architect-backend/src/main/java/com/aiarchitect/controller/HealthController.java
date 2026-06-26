package com.aiarchitect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller — provides a simple endpoint to verify
 * the application is running and can respond to requests.
 *
 * WHY THIS EXISTS:
 * 1. During development: Quick way to verify the app started correctly
 * 2. In production: Used by AWS load balancers and Docker health checks
 *    to determine if the application is healthy
 * 3. For Swagger: Gives us a visible endpoint to test immediately
 *
 * The @Tag annotation groups this endpoint under "Health" in Swagger UI.
 * The @Operation annotation provides a description for the specific endpoint.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check application health", description = "Returns OK if the application is running")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", "AI Software Architect",
                "timestamp", LocalDateTime.now().toString(),
                "version", "0.0.1-SNAPSHOT"
        ));
    }
}
