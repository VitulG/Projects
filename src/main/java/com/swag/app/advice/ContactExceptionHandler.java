package com.swag.app.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ContactExceptionHandler {
    @ExceptionHandler(ContactNotFound.class)
    public ResponseEntity<String> handleContactNotFound() {
        return new ResponseEntity<>("Contact is not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException() {
        return new ResponseEntity<>("There is something wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
