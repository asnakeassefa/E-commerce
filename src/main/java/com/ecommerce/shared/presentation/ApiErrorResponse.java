package com.ecommerce.shared.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response shape for ALL API errors.
 *
 * Example:
 * {
 *   "status": 409,
 *   "error": "Conflict",
 *   "message": "An account with email 'john@example.com' already exists",
 *   "timestamp": "2026-03-20T14:30:00",
 *   "fieldErrors": null
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {

    public static ApiErrorResponse of(int status, String error, String message) {
        return new ApiErrorResponse(status, error, message, LocalDateTime.now(), null);
    }

    public static ApiErrorResponse withFieldErrors(int status, String error, String message,
                                                    Map<String, String> fieldErrors) {
        return new ApiErrorResponse(status, error, message, LocalDateTime.now(), fieldErrors);
    }
}
