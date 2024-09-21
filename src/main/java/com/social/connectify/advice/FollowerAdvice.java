package com.social.connectify.advice;

import com.social.connectify.exceptions.UserAlreadyFollowingException;
import com.social.connectify.exceptions.UserNotFollowingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FollowerAdvice {
    @ExceptionHandler(UserAlreadyFollowingException.class)
    public ResponseEntity<String> handleUserAlreadyFollowingException() {
        return new ResponseEntity<>("User is already following this user", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException() {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFollowingException.class)
    public ResponseEntity<String> handleUserNotFollowingException() {
        return new ResponseEntity<>("User is not following this user", HttpStatus.NOT_FOUND);
    }
}
