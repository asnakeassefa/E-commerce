package com.ecommerce.auth.domain.repository;

import com.ecommerce.auth.domain.entity.AuthToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for AuthToken persistence.
 * The domain defines WHAT it needs — Infrastructure decides HOW.
 */
public interface AuthTokenRepository {

    AuthToken save(AuthToken token);

    Optional<AuthToken> findByToken(String token);

    /**
     * Invalidates all unused tokens of a given type for a user.
     * Called before creating a new token to prevent stale tokens
     * from being valid (e.g., user requests password reset twice).
     */
    void invalidateAllByUserIdAndType(UUID userId, AuthToken.TokenType type);
}
