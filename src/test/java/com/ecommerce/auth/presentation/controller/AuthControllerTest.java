package com.ecommerce.auth.presentation.controller;

import com.ecommerce.auth.application.usecase.*;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.EmailAlreadyExistsException;
import com.ecommerce.auth.domain.exception.InvalidCredentialsException;
import com.ecommerce.auth.domain.exception.InvalidTokenException;
import com.ecommerce.auth.presentation.mapper.AuthMapper;
import com.ecommerce.shared.presentation.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private LoginUserUseCase loginUserUseCase;
    @Mock private RegisterUserUseCase registerUserUseCase;
    @Mock private VerifyEmailUseCase verifyEmailUseCase;
    @Mock private ResendVerificationUseCase resendVerificationUseCase;
    @Mock private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @Mock private ResetPasswordUseCase resetPasswordUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(
                registerUserUseCase,
                loginUserUseCase,
                verifyEmailUseCase,
                resendVerificationUseCase,
                requestPasswordResetUseCase,
                resetPasswordUseCase,
                new AuthMapper());

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── POST /api/v1/auth/login ──────────────────────────────────────

    @Test
    void login_validRequest_returns200WithTokens() throws Exception {
        User user = testUser();
        when(loginUserUseCase.execute(any()))
                .thenReturn(new LoginUserUseCase.Result(user, "access-token", "refresh-token"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john@example.com",
                                    "password": "Password1!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_blankEmail_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"\", \"password\": \"Password1!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void login_invalidEmailFormat_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"not-an-email\", \"password\": \"Password1!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void login_missingPassword_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void login_wrongCredentials_returns401() throws Exception {
        when(loginUserUseCase.execute(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john@example.com\", \"password\": \"Wrong1!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    // ── POST /api/v1/auth/register ───────────────────────────────────

    @Test
    void register_validRequest_returns201WithTokens() throws Exception {
        User user = testUser();
        when(registerUserUseCase.execute(any()))
                .thenReturn(new RegisterUserUseCase.Result(user, "access-token", "refresh-token"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John",
                                    "email": "john@example.com",
                                    "password": "Password1!",
                                    "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void register_missingName_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john@example.com",
                                    "password": "Password1!",
                                    "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void register_missingRole_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John",
                                    "email": "john@example.com",
                                    "password": "Password1!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.role").exists());
    }

    @Test
    void register_passwordTooShort_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John",
                                    "email": "john@example.com",
                                    "password": "Abc1!",
                                    "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void register_invalidRole_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John",
                                    "email": "john@example.com",
                                    "password": "Password1!",
                                    "role": "SUPERUSER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        when(registerUserUseCase.execute(any()))
                .thenThrow(new EmailAlreadyExistsException("john@example.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John",
                                    "email": "john@example.com",
                                    "password": "Password1!",
                                    "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    // ── GET /api/v1/auth/verify-email ────────────────────────────────

    @Test
    void verifyEmail_validToken_returns200() throws Exception {
        when(verifyEmailUseCase.execute("valid-token"))
                .thenReturn(new VerifyEmailUseCase.Result("john@example.com", "Email verified successfully"));

        mockMvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }

    @Test
    void verifyEmail_invalidToken_returns400() throws Exception {
        when(verifyEmailUseCase.execute("bad-token"))
                .thenThrow(new InvalidTokenException("Token not found"));

        mockMvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", "bad-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    // ── POST /api/v1/auth/resend-verification ────────────────────────

    @Test
    void resendVerification_validEmail_returns200() throws Exception {
        when(resendVerificationUseCase.execute("john@example.com"))
                .thenReturn(new ResendVerificationUseCase.Result("Verification email has been resent"));

        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification email has been resent"));
    }

    @Test
    void resendVerification_invalidEmailFormat_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    // ── POST /api/v1/auth/forgot-password ────────────────────────────

    @Test
    void forgotPassword_validEmail_returns200() throws Exception {
        when(requestPasswordResetUseCase.execute("john@example.com"))
                .thenReturn(new RequestPasswordResetUseCase.Result(
                        "If an account with that email exists, a reset link has been sent"));

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void forgotPassword_invalidEmailFormat_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"bad-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    // ── POST /api/v1/auth/reset-password ─────────────────────────────

    @Test
    void resetPassword_validRequest_returns200() throws Exception {
        when(resetPasswordUseCase.execute(any()))
                .thenReturn(new ResetPasswordUseCase.Result("Password has been reset successfully"));

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "token": "reset-token",
                                    "newPassword": "NewPassword1!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password has been reset successfully"));
    }

    @Test
    void resetPassword_missingToken_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\": \"NewPassword1!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.token").exists());
    }

    @Test
    void resetPassword_passwordTooShort_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"reset-token\", \"newPassword\": \"Short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.newPassword").exists());
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private User testUser() {
        return User.reconstitute(
                UUID.randomUUID(), "John", "john@example.com",
                "hashed", Role.CUSTOMER, true, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
