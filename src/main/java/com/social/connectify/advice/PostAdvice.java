package com.social.connectify.advice;

import com.social.connectify.exceptions.PostCreationException;
import com.social.connectify.exceptions.PostNotFoundException;
import com.social.connectify.exceptions.UserPostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PostAdvice {
    @ExceptionHandler(PostCreationException.class)
    public ResponseEntity<String> handlePostCreationException() {
        return new ResponseEntity<>("Failed to create post", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPostNotFoundException.class)
    public ResponseEntity<String> handleUserPostNotFoundException() {
        return new ResponseEntity<>("User post not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException() {
        return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException() {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
