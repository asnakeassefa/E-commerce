package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.domain.entity.AuthToken;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidTokenException;
import com.ecommerce.auth.domain.exception.UserNotFoundException;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;


public class VerifyEmailUseCase {

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;

    public VerifyEmailUseCase(AuthTokenRepository authTokenRepository,
                               UserRepository userRepository) {
        this.authTokenRepository = authTokenRepository;
        this.userRepository = userRepository;
    }

    public Result execute(String token) {
     
        AuthToken authToken = authTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

   
        if (authToken.getType() != AuthToken.TokenType.VERIFY_EMAIL) {
            throw new InvalidTokenException("Wrong token type");
        }

        authToken.validate();


        User user = userRepository.findById(authToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(authToken.getUserId()));

        user.verify();
        userRepository.save(user);

   
        authToken.markUsed();
        authTokenRepository.save(authToken);

        return new Result(user.getEmail(), "Email verified successfully");
    }

    public record Result(String email, String message) {}
}
