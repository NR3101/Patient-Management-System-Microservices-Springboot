package com.neeraj.authservice.exception;

public class InavalidCredentialsException extends RuntimeException {
    public InavalidCredentialsException(String message) {
        super(message);
    }
}
