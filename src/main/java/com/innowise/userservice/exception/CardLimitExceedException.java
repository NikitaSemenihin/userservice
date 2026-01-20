package com.innowise.userservice.exception;

public class CardLimitExceedException extends RuntimeException {
    public CardLimitExceedException(String message) {
        super(message);
    }

    public CardLimitExceedException(String message, Throwable cause) {
        super(message, cause);
    }
}
