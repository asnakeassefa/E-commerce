package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.PasswordEncoder;
import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.application.usecase.LoginUserUseCase;
import com.ecommerce.auth.application.usecase.RegisterUserUseCase;
import com.ecommerce.auth.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AuthBeanConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                    PasswordEncoder passwordEncoder,
                                                    TokenProvider tokenProvider) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, tokenProvider);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepository userRepository,
                                              PasswordEncoder passwordEncoder,
                                              TokenProvider tokenProvider) {
        return new LoginUserUseCase(userRepository, passwordEncoder, tokenProvider);
    }
}
