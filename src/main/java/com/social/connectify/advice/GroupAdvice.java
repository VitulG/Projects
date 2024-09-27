package com.social.connectify.advice;

import com.social.connectify.exceptions.GroupCreationException;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.UserAlreadyInGroupException;
import com.social.connectify.exceptions.UserNotInGroupException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GroupAdvice {

    @ExceptionHandler(GroupCreationException.class)
    public ResponseEntity<String> handleGroupCreationException(GroupCreationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<String> handleGroupNotFoundException(GroupNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotInGroupException.class)
    public ResponseEntity<String> handleUserNotInGroupException(UserNotInGroupException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyInGroupException.class)
    public ResponseEntity<String> handleUserAlreadyInGroupException(UserAlreadyInGroupException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
