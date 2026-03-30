package com.ecommerce.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming JSON payload for POST /api/v1/auth/login
 *
 * Example:
 * {
 *   "email": "john@example.com",
 *   "password": "Secret@123"
 * }
 */
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}
