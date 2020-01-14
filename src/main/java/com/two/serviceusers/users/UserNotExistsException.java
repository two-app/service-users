package com.two.serviceusers.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotExistsException extends ResponseStatusException {
    public UserNotExistsException() {
        super(HttpStatus.NOT_FOUND, "This user does not exist.");
    }
}
