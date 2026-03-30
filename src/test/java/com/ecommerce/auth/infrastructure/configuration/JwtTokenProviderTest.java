package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-only-min-32-chars!!";
    private static final long ACCESS_EXPIRATION  = 900_000L;   // 15 min
    private static final long REFRESH_EXPIRATION = 604_800_000L; // 7 days

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
    }

    @Test
    void generateAccessToken_producesNonBlankToken() {
        User user = testUser();
        String token = provider.generateAccessToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void generateRefreshToken_producesNonBlankToken() {
        User user = testUser();
        String token = provider.generateRefreshToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void generateAccessToken_andRefreshToken_areDifferent() {
        User user = testUser();
        String access  = provider.generateAccessToken(user);
        String refresh = provider.generateRefreshToken(user);
        assertThat(access).isNotEqualTo(refresh);
    }

    @Test
    void validateToken_validAccessToken_returnsTrue() {
        String token = provider.generateAccessToken(testUser());
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_validRefreshToken_returnsTrue() {
        String token = provider.generateRefreshToken(testUser());
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_tamperedToken_returnsFalse() {
        String token = provider.generateAccessToken(testUser());
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void validateToken_randomString_returnsFalse() {
        assertThat(provider.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, -1000L, REFRESH_EXPIRATION);
        String token = shortLived.generateAccessToken(testUser());
        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void extractUserId_returnsCorrectUUID() {
        User user = testUser();
        String token = provider.generateAccessToken(user);
        assertThat(provider.extractUserId(token)).isEqualTo(user.getId());
    }

    @Test
    void extractRole_returnsCorrectRole() {
        User user = testUser();
        String token = provider.generateAccessToken(user);
        assertThat(provider.extractRole(token)).isEqualTo("CUSTOMER");
    }

    @Test
    void validateToken_tokenSignedWithDifferentSecret_returnsFalse() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "completely-different-secret-key-also-32-chars!!", ACCESS_EXPIRATION, REFRESH_EXPIRATION);
        String foreignToken = otherProvider.generateAccessToken(testUser());
        assertThat(provider.validateToken(foreignToken)).isFalse();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private User testUser() {
        return User.reconstitute(
                UUID.randomUUID(), "John", "john@example.com",
                "hashed", Role.CUSTOMER, true, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
