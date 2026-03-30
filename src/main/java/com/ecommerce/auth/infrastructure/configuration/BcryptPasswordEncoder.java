package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt-based password encoder.
 *
 * Strength 12 = ~250ms per hash on modern hardware.
 * Intentionally slow to resist brute-force and GPU cracking attacks.
 */
@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder(12);

    @Override
    public String encode(String rawPpassword) {
        return delegate.encode(rawPpassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
