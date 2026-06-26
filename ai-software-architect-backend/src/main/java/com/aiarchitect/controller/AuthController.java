package com.aiarchitect.controller;

import com.aiarchitect.dto.request.LoginRequest;
import com.aiarchitect.dto.request.RegisterRequest;
import com.aiarchitect.dto.response.AuthResponse;
import com.aiarchitect.model.User;
import com.aiarchitect.service.AuthService;
import com.aiarchitect.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller exposing authentication endpoints.
 *
 * This controller handles:
 * 1. User registration requests (hashing password via {@link AuthService}).
 * 2. User login requests (validating credentials and returning a JWT).
 *
 * Swagger/OpenAPI annotations group these endpoints under "Authentication".
 * Validation annotations (@Valid) trigger automatic input formatting/constraint checks.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     * Returns 201 Created on success.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile with encoded password and defaults to ROLE_USER")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User registeredUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "User registered successfully",
                "userId", registeredUser.getId(),
                "username", registeredUser.getUsername(),
                "email", registeredUser.getEmail()
        ));
    }

    /**
     * Authenticates a user and returns their JWT.
     * Returns 200 OK on success.
     */
    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT token", description = "Authenticates user credentials and returns a valid JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
