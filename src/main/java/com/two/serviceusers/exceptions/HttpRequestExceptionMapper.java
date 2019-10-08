package com.two.serviceusers.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.Collections.singletonList;

@RestControllerAdvice
public class HttpRequestExceptionMapper {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestExceptionMapper.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse error(HttpMessageNotReadableException e) {
        logger.warn("[400] Badly formed HTTP request received.", e);
        return new ErrorResponse(
                singletonList("Badly formed HTTP request.")
        );
    }

}
