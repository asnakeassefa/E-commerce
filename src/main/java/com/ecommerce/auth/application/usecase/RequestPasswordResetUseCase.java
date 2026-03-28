package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;

/**
 * Use Case: Request a password reset.
 *
 * Flow:
 *  1. Look up user by email
 *  2. Invalidate any existing reset tokens for this user
 *  3. Create a new reset token
 *  4. Send the reset email
 *
 * SECURITY NOTE: This endpoint always returns success, even if the
 * email doesn't exist. This prevents email enumeration attacks.
 */
public class RequestPasswordResetUseCase {

    private static final int RESET_TOKEN_EXPIRY_MINUTES = 30; // 30 minutes

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;

    public RequestPasswordResetUseCase(UserRepository userRepository,
                                        AuthTokenRepository authTokenRepository,
                                        EmailService emailService) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
    }


    public Result execute(String email) {
        // Always return success to prevent email enumeration
        userRepository.findByEmail(email).ifPresent(user -> {
            // Invalidate any existing reset tokens
            authTokenRepository.invalidateAllByUserIdAndType(
                    user.getId(), AuthToken.TokenType.RESET_PASSWORD
            );

            // Create a new reset token
            AuthToken resetToken = AuthToken.create(
                    user.getId(),
                    AuthToken.TokenType.RESET_PASSWORD,
                    RESET_TOKEN_EXPIRY_MINUTES
            );
            authTokenRepository.save(resetToken);

            // Send the reset email
            emailService.sendPasswordResetEmail(
                    user.getEmail(), user.getName(), resetToken.getToken()
            );
        });

        return new Result("If an account with that email exists, a reset link has been sent");
    }

    public record Result(String message) {}
}
