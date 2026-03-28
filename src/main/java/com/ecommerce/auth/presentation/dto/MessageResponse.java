package com.ecommerce.auth.presentation.dto;

/**
 * Generic success response for endpoints that don't return
 * entities — just a confirmation message.
 *
 * Used by: verify-email, forgot-password, reset-password, resend-verification
 */
public record MessageResponse(String message) {}
