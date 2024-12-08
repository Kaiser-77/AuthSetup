package com.project.auth_profile_email_exception_app.exceptions;

public class TokenBlacklistedException extends RuntimeException {
    public TokenBlacklistedException(String message) {
        super(message);
    }
}
