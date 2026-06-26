package com.aiarchitect.util;

/**
 * Central location for all application-wide constants.
 *
 * WHY THIS CLASS EXISTS:
 * Instead of scattering magic strings and numbers throughout the codebase,
 * we define them once here. If a value needs to change (like the API version
 * prefix), we change it in ONE place and every class using it updates automatically.
 *
 * This follows the DRY principle (Don't Repeat Yourself).
 */
public final class AppConstants {

    // Private constructor prevents instantiation — this is a utility class
    private AppConstants() {
        throw new IllegalStateException("Utility class — cannot be instantiated");
    }

    // ========================
    // API Versioning
    // ========================
    /** Base path prefix for all API endpoints (e.g., /api/v1/auth/login) */
    public static final String API_BASE_PATH = "/api/v1";

    // ========================
    // Authentication
    // ========================
    /** HTTP header that carries the JWT token */
    public static final String AUTH_HEADER = "Authorization";

    /** Prefix before the JWT token value (e.g., "Bearer eyJhbG...") */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Paths that don't require authentication */
    public static final String[] PUBLIC_URLS = {
            API_BASE_PATH + "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**"
    };

    // ========================
    // Pagination Defaults
    // ========================
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIR = "desc";

    // ========================
    // AI Configuration
    // ========================
    /** Maximum length for a project idea input */
    public static final int MAX_IDEA_LENGTH = 2000;

    /** Minimum length for a project idea input */
    public static final int MIN_IDEA_LENGTH = 10;

    // ========================
    // Export Formats
    // ========================
    public static final String EXPORT_FORMAT_PDF = "pdf";
    public static final String EXPORT_FORMAT_MARKDOWN = "markdown";
    public static final String EXPORT_FORMAT_JSON = "json";

    // ========================
    // User Roles
    // ========================
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
