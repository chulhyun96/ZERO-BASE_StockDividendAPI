package com.cheolhyeon.stockdividends.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = AbstaractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstaractException e) {
        ErrorResponse error = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    }
}
