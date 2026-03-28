package com.ecommerce.auth.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a one-time-use token.
 * Used for both email verification and password reset flows.
 *
 * Lifecycle:
 *   1. Created with a type (VERIFY_EMAIL or RESET_PASSWORD)
 *   2. Sent to user via email
 *   3. User submits the token back
 *   4. Token is validated (exists, not expired, not used)
 *   5. Token is marked as used
 */
public class AuthToken {

    private UUID id;
    private UUID userId;
    private String token;
    private TokenType type;
    private boolean used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public enum TokenType {
        VERIFY_EMAIL,
        RESET_PASSWORD
    }

    // ── Factory: create a new token ──────────────────────────────────
    public static AuthToken create(UUID userId, TokenType type, int expirationMinutes) {
        AuthToken authToken = new AuthToken();
    
        authToken.userId = userId;
        authToken.token = UUID.randomUUID().toString();
        authToken.type = type;
        authToken.used = false;
        authToken.expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);
        authToken.createdAt = LocalDateTime.now();
        return authToken;
    }

    // ── Factory: reconstitute from DB ────────────────────────────────
    public static AuthToken reconstitute(UUID id, UUID userId, String token,
                                          TokenType type, boolean used,
                                          LocalDateTime expiresAt,
                                          LocalDateTime createdAt) {
        AuthToken authToken = new AuthToken();
        authToken.id = id;
        authToken.userId = userId;
        authToken.token = token;
        authToken.type = type;
        authToken.used = used;
        authToken.expiresAt = expiresAt;
        authToken.createdAt = createdAt;
        return authToken;
    }

    // ── Domain validation ────────────────────────────────────────────
    public void validate() {
        if (used) {
            throw new IllegalStateException("This token has already been used");
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new IllegalStateException("This token has expired");
        }
    }

    public void markUsed() {
        this.used = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // ── Getters ──────────────────────────────────────────────────────
    public UUID getId()                 { return id; }
    public UUID getUserId()             { return userId; }
    public String getToken()            { return token; }
    public TokenType getType()          { return type; }
    public boolean isUsed()             { return used; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    private AuthToken() {}
}
