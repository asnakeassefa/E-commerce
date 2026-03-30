package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.exception.InvalidCredentialsException;
import com.ecommerce.auth.domain.repository.UserRepository;



public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

public LoginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
}
public Result execute(Input input){
    User user = userRepository.findByEmail(input.email()).orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(input.password(), user.getPassword())){
        throw new InvalidCredentialsException();
    }

    if (!user.isVerified()) {
            throw new IllegalStateException("Account is not verified. Please check your email.");
        }
    if (!user.isActive()) {
            throw new IllegalStateException("Account is not active. Please wait for admin approval.");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        return new Result(user, accessToken, refreshToken);
    

}

public record Input(String email, String password){}
public record Result(User user, String accessToken, String refreshToken){}



}