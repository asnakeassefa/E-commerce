package com.ecommerce.auth.presentation.mapper;

import com.ecommerce.auth.application.usecase.LoginUserUseCase;
import com.ecommerce.auth.application.usecase.RegisterUserUseCase;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.presentation.dto.AuthResponse;
import com.ecommerce.auth.presentation.dto.LoginRequest;
import com.ecommerce.auth.presentation.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Maps between Presentation DTOs and Application-layer Input/Output objects.
 *
 * This keeps the Controller thin — it doesn't do any conversion logic itself.
 */
@Component
public class AuthMapper {

    public RegisterUserUseCase.Input toRegisterInput(RegisterRequest request) {
        Role role;
        try {
            role = Role.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: '" + request.role()
                    + "'. Must be CUSTOMER or VENDOR");
        }

        return new RegisterUserUseCase.Input(
                request.name(),
                request.email(),
                request.password(),
                role
        );
    }

    public LoginUserUseCase.Input toLoginInput(LoginRequest request) {
        return new LoginUserUseCase.Input(
                request.email(),
                request.password()
        );
    }

    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken
        );
    }
}
