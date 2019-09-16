package com.two.serviceusers.exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String email) {
        super("An account with the email '" + email + "' already exists.");
    }
}
