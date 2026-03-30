package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidCredentialsException;
import com.ecommerce.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenProvider tokenProvider;

    private LoginUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUserUseCase(userRepository, passwordEncoder, tokenProvider);
    }

    @Test
    void execute_validCredentials_returnsTokens() {
        User user = verifiedActiveCustomer();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", user.getPassword())).thenReturn(true);
        when(tokenProvider.generateAccessToken(user)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(user)).thenReturn("refresh-token");

        LoginUserUseCase.Result result = useCase.execute(
                new LoginUserUseCase.Input("john@example.com", "Password1!"));

        assertThat(result.user()).isEqualTo(user);
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void execute_emailNotFound_throwsInvalidCredentials() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new LoginUserUseCase.Input("ghost@example.com", "Password1!")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_wrongPassword_throwsInvalidCredentials() {
        User user = verifiedActiveCustomer();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass1!", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(
                new LoginUserUseCase.Input("john@example.com", "WrongPass1!")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_unverifiedAccount_throwsIllegalState() {
        User user = User.createNew("John", "john@example.com", "hashed", Role.CUSTOMER);
        // verified=false by default
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(
                new LoginUserUseCase.Input("john@example.com", "Password1!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not verified");
    }

    @Test
    void execute_inactiveAccount_throwsIllegalState() {
        User user = verifiedActiveCustomer();
        user.deactivate();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(
                new LoginUserUseCase.Input("john@example.com", "Password1!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not active");
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private User verifiedActiveCustomer() {
        User user = User.createNew("John", "john@example.com", "hashed", Role.CUSTOMER);
        user.verify();
        return user;
    }
}
