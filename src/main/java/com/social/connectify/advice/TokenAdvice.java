package com.social.connectify.advice;

import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.SessionAlreadyActiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TokenAdvice {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException invalidTokenException) {
        return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SessionAlreadyActiveException.class)
    public ResponseEntity<String> handleSessionAlreadyActiveException(SessionAlreadyActiveException
                                                                                  sessionAlreadyActiveException) {
        return new ResponseEntity<>(sessionAlreadyActiveException.getMessage(), HttpStatus.CONFLICT);
    }
}
