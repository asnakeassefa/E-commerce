package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Simple password encoder implementation using SHA-256 + salt.
 *
 * TODO: Replace with BCrypt once spring-boot-starter-security is added.
 *       When you add the security starter, swap this for:
 *           new BCryptPasswordEncoder().encode(raw)
 *           new BCryptPasswordEncoder().matches(raw, encoded)
 *       and delete this class.
 *
 * Stored format: base64(salt):base64(hash)
 */
@Component
public class SimplePasswordEncoder implements PasswordEncoder {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String encode(String rawPassword) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = hashWithSalt(rawPassword, salt);
        return Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split(":");
        if (parts.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        byte[] actualHash = hashWithSalt(rawPassword, salt);
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private byte[] hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
