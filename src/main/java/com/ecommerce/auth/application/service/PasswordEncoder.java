package com.ecommerce.auth.application.service;



public interface PasswordEncoder {
    String encode(String rawPpassword);
    boolean matches(String rawPassword, String encodedPassword);
}