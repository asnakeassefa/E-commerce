package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidTokenException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyEmailUseCaseTest {

    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private UserRepository userRepository;

    private VerifyEmailUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VerifyEmailUseCase(authTokenRepository, userRepository);
    }

    @Test
    void execute_validToken_verifiesUserAndMarksTokenUsed() {
        UUID userId = UUID.randomUUID();
        AuthToken token = validToken(userId, AuthToken.TokenType.VERIFY_EMAIL);
        User user = User.createNew("John", "john@example.com", "hashed", Role.CUSTOMER);

        when(authTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        VerifyEmailUseCase.Result result = useCase.execute("valid-token");

        assertThat(result.message()).isEqualTo("Email verified successfully");
        assertThat(user.isVerified()).isTrue();
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(authTokenRepository).save(token);
    }

    @Test
    void execute_tokenNotFound_throwsInvalidToken() {
        when(authTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("bad-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void execute_wrongTokenType_throwsInvalidToken() {
        UUID userId = UUID.randomUUID();
        AuthToken resetToken = validToken(userId, AuthToken.TokenType.RESET_PASSWORD);

        when(authTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> useCase.execute("reset-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Wrong token type");
    }

    @Test
    void execute_alreadyUsedToken_throwsIllegalState() {
        UUID userId = UUID.randomUUID();
        AuthToken usedToken = AuthToken.reconstitute(
                UUID.randomUUID(), userId, "used-token",
                AuthToken.TokenType.VERIFY_EMAIL,
                true,                               // used = true
                LocalDateTime.now().plusHours(24),
                LocalDateTime.now().minusHours(1));

        when(authTokenRepository.findByToken("used-token")).thenReturn(Optional.of(usedToken));

        assertThatThrownBy(() -> useCase.execute("used-token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been used");
    }

    @Test
    void execute_expiredToken_throwsIllegalState() {
        UUID userId = UUID.randomUUID();
        AuthToken expiredToken = AuthToken.reconstitute(
                UUID.randomUUID(), userId, "expired-token",
                AuthToken.TokenType.VERIFY_EMAIL,
                false,
                LocalDateTime.now().minusHours(1),  // expiresAt in the past
                LocalDateTime.now().minusHours(25));

        when(authTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> useCase.execute("expired-token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expired");
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private AuthToken validToken(UUID userId, AuthToken.TokenType type) {
        return AuthToken.reconstitute(
                UUID.randomUUID(), userId, "valid-token",
                type, false,
                LocalDateTime.now().plusHours(24),
                LocalDateTime.now());
    }
}
