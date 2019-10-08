package com.two.serviceusers.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionMapper {

    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    /**
     * @param e Constraint Violation Exception, typically raised by JavaX Validation Constraints.
     * @return a list of user-friendly errors, extracted from each constraint violation.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse error(ConstraintViolationException e) {
        logger.warn("[400] Converting multiple Constraint Violations into 400 Bad Request.", e);
        return new ErrorResponse(
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse error(MethodArgumentNotValidException e) {
        logger.warn("[400] Converting singular Constraint Violation into 400 Bad Request.", e);
        return new ErrorResponse(
                e.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList())
        );
    }

}
