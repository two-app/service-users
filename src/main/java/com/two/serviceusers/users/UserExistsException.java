package com.two.serviceusers.users;

import com.two.serviceusers.exceptions.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.Collections.singletonList;

class UserExistsException extends RuntimeException {
    UserExistsException(String email) {
        super("An account with the email '" + email + "' already exists.");
    }
}

@RestControllerAdvice
class UserExistsExceptionMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserExistsExceptionMapper.class);

    /**
     * @param e Constraint Violation Exception, typically raised by JavaX Validation Constraints.
     * @return a list of user-friendly errors, extracted from each constraint violation.
     */
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse error(UserExistsException e) {
        logger.warn("[400] Converting UserExistsException into 400 Bad Request.", e);
        return new ErrorResponse(singletonList(e.getMessage()));
    }

}
