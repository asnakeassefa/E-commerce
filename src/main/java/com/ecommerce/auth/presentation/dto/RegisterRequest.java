package com.ecommerce.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Incoming JSON payload for POST /api/v1/auth/register
 *
 * Example:
 * {
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "password": "Secret@123",
 *   "role": "CUSTOMER"
 * }
 */
public record RegisterRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotNull(message = "Role is required (CUSTOMER or VENDOR)")
        String role
) {}
