package com.ecommerce.auth.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthTokenTest {

    @Test
    void create_setsFieldsCorrectly() {
        UUID userId = UUID.randomUUID();
        AuthToken token = AuthToken.create(userId, AuthToken.TokenType.VERIFY_EMAIL, 60);

        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getType()).isEqualTo(AuthToken.TokenType.VERIFY_EMAIL);
        assertThat(token.isUsed()).isFalse();
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(token.getCreatedAt()).isNotNull();
    }

    @Test
    void create_tokenValueIsUniqueEachTime() {
        UUID userId = UUID.randomUUID();
        AuthToken t1 = AuthToken.create(userId, AuthToken.TokenType.VERIFY_EMAIL, 60);
        AuthToken t2 = AuthToken.create(userId, AuthToken.TokenType.VERIFY_EMAIL, 60);

        assertThat(t1.getToken()).isNotEqualTo(t2.getToken());
    }

    @Test
    void validate_validToken_doesNotThrow() {
        AuthToken token = validToken();
        // should not throw
        token.validate();
    }

    @Test
    void validate_usedToken_throwsIllegalState() {
        AuthToken token = usedToken();

        assertThatThrownBy(token::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been used");
    }

    @Test
    void validate_expiredToken_throwsIllegalState() {
        AuthToken token = expiredToken();

        assertThatThrownBy(token::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void markUsed_setsUsedToTrue() {
        AuthToken token = validToken();
        assertThat(token.isUsed()).isFalse();

        token.markUsed();

        assertThat(token.isUsed()).isTrue();
    }

    @Test
    void isExpired_futureExpiry_returnsFalse() {
        AuthToken token = validToken();
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void isExpired_pastExpiry_returnsTrue() {
        AuthToken token = expiredToken();
        assertThat(token.isExpired()).isTrue();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private AuthToken validToken() {
        return AuthToken.reconstitute(
                UUID.randomUUID(), UUID.randomUUID(), "token-value",
                AuthToken.TokenType.VERIFY_EMAIL, false,
                LocalDateTime.now().plusHours(24), LocalDateTime.now());
    }

    private AuthToken usedToken() {
        return AuthToken.reconstitute(
                UUID.randomUUID(), UUID.randomUUID(), "token-value",
                AuthToken.TokenType.VERIFY_EMAIL, true,
                LocalDateTime.now().plusHours(24), LocalDateTime.now());
    }

    private AuthToken expiredToken() {
        return AuthToken.reconstitute(
                UUID.randomUUID(), UUID.randomUUID(), "token-value",
                AuthToken.TokenType.VERIFY_EMAIL, false,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(25));
    }
}
