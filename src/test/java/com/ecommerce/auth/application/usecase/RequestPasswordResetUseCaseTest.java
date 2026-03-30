package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private EmailService emailService;

    private RequestPasswordResetUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RequestPasswordResetUseCase(userRepository, authTokenRepository, emailService);
    }

    @Test
    void execute_existingEmail_invalidatesOldTokensCreatesNewAndSendsEmail() {
        User user = activeUser();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RequestPasswordResetUseCase.Result result = useCase.execute("john@example.com");

        assertThat(result.message()).contains("reset link");
        verify(authTokenRepository).invalidateAllByUserIdAndType(
                user.getId(), AuthToken.TokenType.RESET_PASSWORD);
        verify(authTokenRepository).save(any());
        verify(emailService).sendPasswordResetEmail(eq("john@example.com"), eq("John"), anyString());
    }

    @Test
    void execute_unknownEmail_returnsSuccessWithoutRevealingExistence() {
        // Security: must NOT throw or behave differently for unknown emails (prevents enumeration)
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        RequestPasswordResetUseCase.Result result = useCase.execute("ghost@example.com");

        assertThat(result.message()).contains("reset link");
        verify(authTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private User activeUser() {
        return User.reconstitute(UUID.randomUUID(), "John", "john@example.com",
                "hashed", Role.CUSTOMER, true, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
