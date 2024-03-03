package com.bob.bankapispringapp.exception;

public class InvalidBirthdateException extends RuntimeException {
    public InvalidBirthdateException(String message) {
        super(message);
    }
}

