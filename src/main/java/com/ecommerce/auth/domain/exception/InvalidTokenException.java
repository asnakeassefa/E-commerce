package com.ecommerce.auth.domain.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String reason) {
        super("Invalid token: " + reason);
    }
}
