package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.EmailAlreadyExistsException;
import com.ecommerce.auth.domain.repository.UserRepository;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.application.service.EmailService;

/**
 * Use Case: Register a new CUSTOMER or VENDOR.
 *
 * Flow:
 *  1. Check email uniqueness
 *  2. Validate domain rules
 *  3. Hash the password
 *  4. Persist the user
 *  5. Return tokens
 */
public class RegisterUserUseCase {
    private static final int VERIFICATION_TOKEN_EXPIRY_MINUTES = 60 * 24; // 24 hours

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    public RegisterUserUseCase(UserRepository userRepository,
                                AuthTokenRepository authTokenRepository,
                                PasswordEncoder passwordEncoder,
                                TokenProvider tokenProvider,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
         this.emailService = emailService;
    }


    public Result execute(Input input) {
        // 1. Check uniqueness


        System.out.println("__________________________________________"+input.email() + "+++++++++++++++++" + input.password()+"+++++++"+input.name()+ "+++++++" + input.role()+"_____________________________________________");
        if (userRepository.existsByEmail(input.email())) {
            throw new EmailAlreadyExistsException(input.email());
        }

        // 2. Validate password strength (business rule from PRD: min 8 chars, upper/lower/number/symbol)
        validatePasswordStrength(input.password());

        // 3. Hash and create domain entity
        String hashed = passwordEncoder.encode(input.password());
        User user = User.createNew(input.name(), input.email(), hashed, input.role());
        user.validateForRegistration();
        System.out.println("__________________________________________"+user.isVerified() + "+++++++++++++++++" + input.password()+"+++++++"+input.name()+ "+++++++" + input.role()+"_____________________________________________");

        // 4. Persist
        User savedUser = userRepository.save(user);
        
        AuthToken verificationToken = AuthToken.create(
                savedUser.getId(),
                AuthToken.TokenType.VERIFY_EMAIL,
                VERIFICATION_TOKEN_EXPIRY_MINUTES
        );
        authTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(
                savedUser.getEmail(), savedUser.getName(), verificationToken.getToken()
        );

        // 5. Generate tokens
        String accessToken = tokenProvider.generateAccessToken(savedUser);
        String refreshToken = tokenProvider.generateRefreshToken(savedUser);

        return new Result(savedUser, accessToken, refreshToken);
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

    // ── Input/Output records ─────────────────────────────────────────
    public record Input(String name, String email, String password, Role role) {}

    public record Result(User user, String accessToken, String refreshToken) {}
}
