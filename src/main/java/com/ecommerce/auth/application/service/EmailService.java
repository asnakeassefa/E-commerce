package com.ecommerce.auth.application.service;

/**
 * Port for sending emails.
 * The use cases call this interface without knowing whether
 * it's SMTP, SendGrid, AWS SES, or just a console logger.
 */
public interface EmailService {

    
    void sendVerificationEmail(String toEmail, String userName, String token);
    void sendPasswordResetEmail(String toEmail, String userName, String token);
}
