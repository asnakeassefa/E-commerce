package com.ecommerce.auth.presentation.controller;

import com.ecommerce.auth.application.usecase.LoginUserUseCase;
import com.ecommerce.auth.application.usecase.RegisterUserUseCase;
import com.ecommerce.auth.presentation.dto.AuthResponse;
import com.ecommerce.auth.presentation.dto.LoginRequest;
import com.ecommerce.auth.presentation.dto.RegisterRequest;
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
    private final AuthMapper authMapper;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                           LoginUserUseCase loginUserUseCase,
                           AuthMapper authMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginUserUseCase.Input input = authMapper.toLoginInput(request);
        LoginUserUseCase.Result result = loginUserUseCase.execute(input);

        AuthResponse response = authMapper.toAuthResponse(
                result.user(), result.accessToken(), result.refreshToken()
        );

        return ResponseEntity.ok(response);
    }
}
