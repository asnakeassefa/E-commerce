package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidTokenException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class ResetPasswordUseCaseTest {

    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private ResetPasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResetPasswordUseCase(authTokenRepository, userRepository, passwordEncoder);
    }

    @Test
    void execute_validToken_updatesPasswordAndInvalidatesTokens() {
        UUID userId = UUID.randomUUID();
        AuthToken token = validResetToken(userId);
        User user = activeUser(userId);

        when(authTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("new-hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResetPasswordUseCase.Result result = useCase.execute(
                new ResetPasswordUseCase.Input("reset-token", "NewPassword1!"));

        assertThat(result.message()).contains("reset successfully");
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(authTokenRepository).invalidateAllByUserIdAndType(userId, AuthToken.TokenType.RESET_PASSWORD);
    }

    @Test
    void execute_tokenNotFound_throwsInvalidToken() {
        when(authTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new ResetPasswordUseCase.Input("bad-token", "NewPassword1!")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void execute_wrongTokenType_throwsInvalidToken() {
        UUID userId = UUID.randomUUID();
        AuthToken verifyToken = AuthToken.reconstitute(
                UUID.randomUUID(), userId, "verify-token",
                AuthToken.TokenType.VERIFY_EMAIL, false,
                LocalDateTime.now().plusHours(1), LocalDateTime.now());

        when(authTokenRepository.findByToken("verify-token")).thenReturn(Optional.of(verifyToken));

        assertThatThrownBy(() -> useCase.execute(
                new ResetPasswordUseCase.Input("verify-token", "NewPassword1!")))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Wrong token type");
    }

    @Test
    void execute_expiredToken_throwsIllegalState() {
        UUID userId = UUID.randomUUID();
        AuthToken expiredToken = AuthToken.reconstitute(
                UUID.randomUUID(), userId, "expired-token",
                AuthToken.TokenType.RESET_PASSWORD, false,
                LocalDateTime.now().minusMinutes(31),   // expired 1 minute ago
                LocalDateTime.now().minusHours(1));

        when(authTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> useCase.execute(
                new ResetPasswordUseCase.Input("expired-token", "NewPassword1!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void execute_alreadyUsedToken_throwsIllegalState() {
        UUID userId = UUID.randomUUID();
        AuthToken usedToken = AuthToken.reconstitute(
                UUID.randomUUID(), userId, "used-token",
                AuthToken.TokenType.RESET_PASSWORD, true,   // used = true
                LocalDateTime.now().plusMinutes(30), LocalDateTime.now());

        when(authTokenRepository.findByToken("used-token")).thenReturn(Optional.of(usedToken));

        assertThatThrownBy(() -> useCase.execute(
                new ResetPasswordUseCase.Input("used-token", "NewPassword1!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been used");
    }

    @ParameterizedTest(name = "weak password: \"{0}\"")
    @ValueSource(strings = {
            "Short1!",
            "nouppercase1!",
            "NOLOWERCASE1!",
            "NoDigitHere!",
            "NoSpecialChar1"
    })
    void execute_weakNewPassword_throwsIllegalArgument(String weakPassword) {
        UUID userId = UUID.randomUUID();
        AuthToken token = validResetToken(userId);
        when(authTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(
                new ResetPasswordUseCase.Input("reset-token", weakPassword)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private AuthToken validResetToken(UUID userId) {
        return AuthToken.reconstitute(
                UUID.randomUUID(), userId, "reset-token",
                AuthToken.TokenType.RESET_PASSWORD, false,
                LocalDateTime.now().plusMinutes(30), LocalDateTime.now());
    }

    private User activeUser(UUID userId) {
        return User.reconstitute(userId, "John", "john@example.com",
                "old-hashed", Role.CUSTOMER, true, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
