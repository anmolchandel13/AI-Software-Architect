package com.aiarchitect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource (User, Project, Report) is not found in the database.
 *
 * WHY THIS EXISTS:
 * Instead of returning a generic 500 error when something isn't found, we throw this
 * specific exception. Our GlobalExceptionHandler (created in Milestone 8) will catch it
 * and return a clean 404 response with a meaningful message to the client.
 *
 * The @ResponseStatus annotation tells Spring to automatically return a 404 HTTP status
 * if this exception escapes to the framework level.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * @param resourceName  The type of resource (e.g., "Project", "User")
     * @param fieldName     The field used to search (e.g., "id", "email")
     * @param fieldValue    The value that was searched for (e.g., 42, "john@email.com")
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
