package com.ecommerce.auth.infrastructure.configuration;

import com.ecommerce.auth.application.service.EmailService;
import org.springframework.stereotype.Component;


 
@Component
public class ConsoleEmailService implements EmailService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/auth";

    @Override
    public void sendVerificationEmail(String toEmail, String userName, String token) {
        String verifyLink = BASE_URL + "/verify-email?token=" + token;

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  EMAIL VERIFICATION (dev - not actually sent)");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  To:      " + toEmail);
        System.out.println("  Name:    " + userName);
        System.out.println("  Token:   " + token);
        System.out.println("  Link:    " + verifyLink);
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String userName, String token) {
        String resetLink = BASE_URL + "/reset-password?token=" + token;

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  PASSWORD RESET (dev - not actually sent)");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  To:      " + toEmail);
        System.out.println("  Name:    " + userName);
        System.out.println("  Token:   " + token);
        System.out.println("  Link:    " + resetLink);
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
    }
}
