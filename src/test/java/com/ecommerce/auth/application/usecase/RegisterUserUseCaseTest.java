package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.EmailAlreadyExistsException;
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
class RegisterUserUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenProvider tokenProvider;
    @Mock private EmailService emailService;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(
                userRepository, authTokenRepository, passwordEncoder, tokenProvider, emailService);
    }

    @Test
    void execute_validInput_savesUserSendsEmailAndReturnsTokens() {
        User savedUser = User.reconstitute(
                UUID.randomUUID(), "John", "john@example.com", "hashed",
                Role.CUSTOMER, false, true, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateAccessToken(savedUser)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(savedUser)).thenReturn("refresh-token");

        RegisterUserUseCase.Result result = useCase.execute(
                new RegisterUserUseCase.Input("John", "john@example.com", "Password1!", Role.CUSTOMER));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(any());
        verify(authTokenRepository).save(any());
        verify(emailService).sendVerificationEmail(eq("john@example.com"), eq("John"), anyString());
    }

    @Test
    void execute_vendorRole_savedAsInactive() {
        User savedVendor = User.reconstitute(
                UUID.randomUUID(), "Jane", "jane@example.com", "hashed",
                Role.VENDOR, false, false, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedVendor);
        when(authTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateAccessToken(savedVendor)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(savedVendor)).thenReturn("refresh-token");

        RegisterUserUseCase.Result result = useCase.execute(
                new RegisterUserUseCase.Input("Jane", "jane@example.com", "Password1!", Role.VENDOR));

        assertThat(result.user().isActive()).isFalse();
    }

    @Test
    void execute_duplicateEmail_throwsEmailAlreadyExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input("John", "john@example.com", "Password1!", Role.CUSTOMER)))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @ParameterizedTest(name = "weak password: \"{0}\"")
    @ValueSource(strings = {
            "Short1!",          // too short (7 chars)
            "nouppercase1!",    // no uppercase
            "NOLOWERCASE1!",    // no lowercase
            "NoDigitHere!",     // no digit
            "NoSpecialChar1"    // no special character
    })
    void execute_weakPassword_throwsIllegalArgument(String weakPassword) {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input("John", "john@example.com", weakPassword, Role.CUSTOMER)))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
    }
}
