package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.UserNotFoundException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResendVerificationUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private EmailService emailService;

    private ResendVerificationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResendVerificationUseCase(userRepository, authTokenRepository, emailService);
    }

    @Test
    void execute_unverifiedUser_invalidatesOldTokensAndSendsNewEmail() {
        User user = unverifiedUser();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResendVerificationUseCase.Result result = useCase.execute("john@example.com");

        assertThat(result.message()).contains("resent");
        verify(authTokenRepository).invalidateAllByUserIdAndType(
                user.getId(), AuthToken.TokenType.VERIFY_EMAIL);
        verify(authTokenRepository).save(any());
        verify(emailService).sendVerificationEmail(eq("john@example.com"), eq("John"), anyString());
    }

    @Test
    void execute_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("ghost@example.com"))
                .isInstanceOf(UserNotFoundException.class);

        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void execute_alreadyVerifiedUser_throwsIllegalState() {
        User user = unverifiedUser();
        user.verify();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute("john@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already verified");

        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private User unverifiedUser() {
        // reconstitute so we have a real UUID for verify on the authTokenRepository call
        return User.reconstitute(UUID.randomUUID(), "John", "john@example.com",
                "hashed", Role.CUSTOMER, false, true,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
    }
}
