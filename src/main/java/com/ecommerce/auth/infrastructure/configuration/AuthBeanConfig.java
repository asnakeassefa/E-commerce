package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.EmailService;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.repository.AuthTokenRepository;
import com.ecommerce.auth.domain.repository.UserRepository;
import com.ecommerce.auth.application.usecase.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ecommerce.auth.application.service.EmailService;

@Configuration
public class AuthBeanConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                    AuthTokenRepository authTokenRepository,
                                                    PasswordEncoder passwordEncoder,
                                                    TokenProvider tokenProvider,
                                                    EmailService emailService) {
        return new RegisterUserUseCase(userRepository,authTokenRepository, passwordEncoder, tokenProvider, emailService);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepository userRepository,
                                              PasswordEncoder passwordEncoder,
                                              TokenProvider tokenProvider) {
        return new LoginUserUseCase(userRepository, passwordEncoder, tokenProvider);
    }
      @Bean
    public VerifyEmailUseCase verifyEmailUseCase(AuthTokenRepository authTokenRepository,
                                                  UserRepository userRepository) {
        return new VerifyEmailUseCase(authTokenRepository, userRepository);
    }

    @Bean
    public ResendVerificationUseCase resendVerificationUseCase(UserRepository userRepository,
                                                                AuthTokenRepository authTokenRepository,
                                                                EmailService emailService) {
        return new ResendVerificationUseCase(userRepository, authTokenRepository, emailService);
    }

    @Bean
    public RequestPasswordResetUseCase requestPasswordResetUseCase(UserRepository userRepository,
                                                                    AuthTokenRepository authTokenRepository,
                                                                    EmailService emailService) {
        return new RequestPasswordResetUseCase(userRepository, authTokenRepository, emailService);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(AuthTokenRepository authTokenRepository,
                                                      UserRepository userRepository,
                                                      PasswordEncoder passwordEncoder) {
        return new ResetPasswordUseCase(authTokenRepository, userRepository, passwordEncoder);
    }
}
