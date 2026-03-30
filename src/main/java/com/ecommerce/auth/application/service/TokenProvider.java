package com.ecommerce.auth.application.service;

import com.ecommerce.auth.domain.entity.User;

import java.util.UUID;

/**
 * Port for token generation and validation.
 * The use case asks for a token — it doesn't know if it's
 * JWT, OAuth, or anything else under the hood.
 */
public interface TokenProvider {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateToken(String token);

    UUID extractUserId(String token);

    String extractRole(String token);
}
