package com.aiarchitect.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response returned by ALL error handlers.
 *
 * WHY THIS EXISTS:
 * Without this, different errors return different JSON shapes — a validation
 * error might return { "errors": [...] } while a 404 returns { "message": "..." }.
 * That forces the frontend to handle each format differently.
 *
 * With ApiErrorResponse, EVERY error the frontend receives has the same structure:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Project not found with id: 42",
 *   "timestamp": "2024-01-15T10:30:00",
 *   "path": "/api/v1/projects/42",
 *   "validationErrors": null  <-- only present for validation failures
 * }
 *
 * @JsonInclude(NON_NULL) means null fields are omitted from the JSON entirely,
 * keeping responses clean.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /** HTTP status code (e.g., 400, 401, 404, 500) */
    private int status;

    /** HTTP status reason phrase (e.g., "Bad Request", "Not Found") */
    private String error;

    /** Human-readable error description */
    private String message;

    /** When the error occurred */
    private LocalDateTime timestamp;

    /** The API path that was called */
    private String path;

    /**
     * Field-level validation errors (only present for 400 validation failures).
     * Key = field name (e.g., "email"), Value = error message (e.g., "must not be blank")
     */
    private Map<String, String> validationErrors;
}
