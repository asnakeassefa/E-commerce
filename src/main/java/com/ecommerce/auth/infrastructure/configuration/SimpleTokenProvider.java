package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.TokenProvider;
import com.ecommerce.auth.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

/**
 * Stub token provider using simple random tokens.
 *
 * TODO: Replace with a real JWT implementation once you add these to pom.xml:
 *
 *   <dependency>
 *       <groupId>io.jsonwebtoken</groupId>
 *       <artifactId>jjwt-api</artifactId>
 *       <version>0.12.6</version>
 *   </dependency>
 *   <dependency>
 *       <groupId>io.jsonwebtoken</groupId>
 *       <artifactId>jjwt-impl</artifactId>
 *       <version>0.12.6</version>
 *       <scope>runtime</scope>
 *   </dependency>
 *   <dependency>
 *       <groupId>io.jsonwebtoken</groupId>
 *       <artifactId>jjwt-jackson</artifactId>
 *       <version>0.12.6</version>
 *       <scope>runtime</scope>
 *   </dependency>
 *
 * Then create a JwtTokenProvider class that signs tokens with a secret,
 * embeds userId/role/email as claims, and sets expiry (15 min access, 7 day refresh).
 */
@Component
public class SimpleTokenProvider implements TokenProvider {

    @Override
    public String generateAccessToken(User user) {
        // Placeholder — returns a base64 token embedding userId
        String payload = "access:" + user.getId() + ":" + user.getRole() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    @Override
    public String generateRefreshToken(User user) {
        String payload = "refresh:" + user.getId() + ":" + UUID.randomUUID() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
}
