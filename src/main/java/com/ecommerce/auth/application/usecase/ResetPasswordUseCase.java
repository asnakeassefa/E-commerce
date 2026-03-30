package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidTokenException;
import com.ecommerce.auth.domain.exception.UserNotFoundException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;

/**
 * Use Case: Reset a user's password using a valid reset token.
 *
 * Flow:
 *  1. Find and validate the reset token
 *  2. Validate new password strength
 *  3. Hash the new password
 *  4. Update the user's password
 *  5. Mark the token as used
 *  6. Invalidate all other reset tokens for this user
 */
public class ResetPasswordUseCase {

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(AuthTokenRepository authTokenRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.authTokenRepository = authTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Result execute(Input input) {
    
        AuthToken authToken = authTokenRepository.findByToken(input.token())
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

     
        if (authToken.getType() != AuthToken.TokenType.RESET_PASSWORD) {
            throw new InvalidTokenException("Wrong token type");
        }

        authToken.validate();

        validatePasswordStrength(input.newPassword());

       
        User user = userRepository.findById(authToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(authToken.getUserId()));

        String hashedPassword = passwordEncoder.encode(input.newPassword());
        user.updatePassword(hashedPassword);
        userRepository.save(user);

        
        authToken.markUsed();
        authTokenRepository.save(authToken);

        
        authTokenRepository.invalidateAllByUserIdAndType(
                user.getId(), AuthToken.TokenType.RESET_PASSWORD
        );

        return new Result("Password has been reset successfully");
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain an uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain a lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain a digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain a special character");
        }
    }

    public record Input(String token, String newPassword) {}
    public record Result(String message) {}
}
