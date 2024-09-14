package com.social.connectify.advice;

import com.social.connectify.exceptions.FriendNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FriendAdvice {

    @ExceptionHandler(FriendNotFoundException.class)
    public ResponseEntity<String> handleFriendNotFound() {
        return new ResponseEntity<>("Friend not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
