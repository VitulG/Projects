package com.social.connectify.advice;

import com.social.connectify.exceptions.EventCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EventAdvice {
    @ExceptionHandler(EventCreationException.class)
    public ResponseEntity<String> handleEventCreationException() {
        return new ResponseEntity<>("Failed to create event", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
