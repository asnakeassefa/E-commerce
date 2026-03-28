package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.UserNotFoundException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;

/**
 * Use Case: Resend email verification link.
 *
 * Flow:
 *  1. Find user by email
 *  2. Check if already verified
 *  3. Invalidate old verification tokens
 *  4. Create new token and send email
 */
public class ResendVerificationUseCase {

    private static final int VERIFICATION_TOKEN_EXPIRY_MINUTES = 60 * 24; // 24 hours

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;

    public ResendVerificationUseCase(UserRepository userRepository,
                                      AuthTokenRepository authTokenRepository,
                                      EmailService emailService) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
    }

    public Result execute(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        // Invalidate old tokens
        authTokenRepository.invalidateAllByUserIdAndType(
                user.getId(), AuthToken.TokenType.VERIFY_EMAIL
        );

        // Create new token
        AuthToken token = AuthToken.create(
                user.getId(),
                AuthToken.TokenType.VERIFY_EMAIL,
                VERIFICATION_TOKEN_EXPIRY_MINUTES
        );
        authTokenRepository.save(token);

        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token.getToken());

        return new Result("Verification email has been resent");
    }

    public record Result(String message) {}
}
