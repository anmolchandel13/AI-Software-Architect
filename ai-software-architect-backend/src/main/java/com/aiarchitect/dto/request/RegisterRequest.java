package com.aiarchitect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 *
 * WHY DTOs EXIST (this applies to ALL DTOs):
 * We NEVER accept raw JPA entity objects from the client. Why?
 * 1. SECURITY: The client could set fields we don't want them to (like "role" or "id").
 * 2. VALIDATION: DTOs carry validation annotations; entities carry JPA annotations.
 *    Mixing both on one class creates a mess.
 * 3. DECOUPLING: If we change the database schema, the API contract doesn't break.
 *
 * The @Valid annotation on the controller parameter triggers automatic validation.
 * If any constraint fails, Spring returns a 400 Bad Request before our code even runs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
}
