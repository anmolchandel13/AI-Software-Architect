package com.aiarchitect.exception;

/**
 * Thrown when a user tries to access a resource they don't own,
 * or when JWT validation fails for reasons beyond simple expiry.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
