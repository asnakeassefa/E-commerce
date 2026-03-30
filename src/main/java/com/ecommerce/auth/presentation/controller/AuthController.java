package com.ecommerce.auth.presentation.controller;
// import org.springframework.security.access.prepost.PreAuthorize;
import com.ecommerce.auth.application.usecase.*;
import com.ecommerce.auth.presentation.dto.*;
import com.ecommerce.auth.presentation.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * Endpoints:
 *   POST /api/v1/auth/register  — Register a new user
 *   POST /api/v1/auth/login     — Authenticate and receive tokens
 *
 * This controller is intentionally thin:
 *   1. Receive + validate the DTO (Jakarta Validation)
 *   2. Map DTO → Use Case Input
 *   3. Execute the Use Case
 *   4. Map result → Response DTO
 *   5. Return HTTP response
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final AuthMapper authMapper;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                           LoginUserUseCase loginUserUseCase,
                           VerifyEmailUseCase verifyEmailUseCase,
                           ResendVerificationUseCase resendVerificationUseCase,
                           RequestPasswordResetUseCase requestPasswordResetUseCase,
                           ResetPasswordUseCase resetPasswordUseCase,
                           AuthMapper authMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
        this.resendVerificationUseCase = resendVerificationUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.authMapper = authMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        RegisterUserUseCase.Input input = authMapper.toRegisterInput(request);
        RegisterUserUseCase.Result result = registerUserUseCase.execute(input);

        AuthResponse response = authMapper.toAuthResponse(
                result.user(), result.accessToken(), result.refreshToken()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    // @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginUserUseCase.Input input = authMapper.toLoginInput(request);
        LoginUserUseCase.Result result = loginUserUseCase.execute(input);

        AuthResponse response = authMapper.toAuthResponse(
                result.user(), result.accessToken(), result.refreshToken()
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        VerifyEmailUseCase.Result result = verifyEmailUseCase.execute(token);
        return ResponseEntity.ok(new MessageResponse(result.message()));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request) {
        ResendVerificationUseCase.Result result = resendVerificationUseCase.execute(request.email());
        return ResponseEntity.ok(new MessageResponse(result.message()));
    }

    // ── Password Reset ───────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        RequestPasswordResetUseCase.Result result = requestPasswordResetUseCase.execute(request.email());
        return ResponseEntity.ok(new MessageResponse(result.message()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordUseCase.Input input = new ResetPasswordUseCase.Input(
                request.token(), request.newPassword()
        );
        ResetPasswordUseCase.Result result = resetPasswordUseCase.execute(input);
        return ResponseEntity.ok(new MessageResponse(result.message()));
    }
}
