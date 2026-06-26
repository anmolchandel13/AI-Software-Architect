package com.aiarchitect.exception;

/**
 * Thrown when the Gemini AI API call fails — network error, rate limit, invalid response, etc.
 *
 * WHY THIS EXISTS:
 * AI API calls are inherently unreliable (network issues, rate limits, malformed responses).
 * This custom exception lets us handle AI-specific failures differently from database errors
 * or validation errors, giving the user a clear message like "AI service is temporarily
 * unavailable" instead of a cryptic stack trace.
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
