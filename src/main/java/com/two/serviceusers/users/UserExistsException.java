package com.two.serviceusers.users;

class UserExistsException extends RuntimeException {
    UserExistsException(String email) {
        super("An account with the email '" + email + "' already exists.");
    }
}
