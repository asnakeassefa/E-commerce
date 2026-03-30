package com.ecommerce.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming JSON for POST /api/v1/auth/reset-password
 *
 * Example:
 * {
 *   "token": "a1b2c3d4-e5f6-...",
 *   "newPassword": "NewSecret@456"
 * }
 */
public record ResetPasswordRequest(

        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword
) {}
