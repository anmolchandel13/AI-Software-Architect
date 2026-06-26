package com.aiarchitect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after successful login or registration.
 *
 * Contains the JWT token that the frontend must store and include
 * in the Authorization header of all subsequent requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** The JWT token string (e.g., "eyJhbGciOiJIUzI1NiIs...") */
    private String token;

    /** Token type — always "Bearer" for JWT */
    @Builder.Default
    private String tokenType = "Bearer";

    /** The authenticated user's ID */
    private Long userId;

    /** The authenticated user's username */
    private String username;

    /** The authenticated user's email */
    private String email;
}
