package com.ecommerce.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming JSON for POST /api/v1/auth/forgot-password
 *
 * Example: { "email": "john@example.com" }
 */
public record ForgotPasswordRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}
