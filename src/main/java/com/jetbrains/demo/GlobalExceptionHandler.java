package com.jetbrains.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handle(UserAlreadyExistsException e) {
        var error = e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
