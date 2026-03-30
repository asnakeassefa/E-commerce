package com.ecommerce.auth.presentation.dto;

import java.util.UUID;

/**
 * Outgoing JSON response for both register and login endpoints.
 *
 * Example response:
 * {
 *   "userId": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "role": "CUSTOMER",
 *   "accessToken": "eyJhbGciOi...",
 *   "refreshToken": "dGhpcyBpcyBh..."
 * }
 */
public record AuthResponse(
        UUID userId,
        String name,
        String email,
        String role,
        String accessToken,
        String refreshToken
) {}
