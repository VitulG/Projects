package com.govt.irctc.advice.UserAdvice;

import com.govt.irctc.exceptions.SecurityExceptions.InvalidCredentialsException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.UserExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return new ResponseEntity<>("an unexpected error. please try again", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<String> handleUserCreationException(UserCreationException userCreationException) {
        return new ResponseEntity<>(userCreationException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException userAlreadyExistsException) {
        return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException invalidCredentialsException) {
        return new ResponseEntity<>(invalidCredentialsException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handleTokenNotFoundException(TokenNotFoundException tokenNotFoundException) {
        return new ResponseEntity<>(tokenNotFoundException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<String> handleUnauthorizedUserException(UnauthorizedUserException unauthorizedUserException) {
        return new ResponseEntity<>(unauthorizedUserException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserUpdationException.class)
    public ResponseEntity<String> handleUserUpdationException(UserUpdationException userUpdationException) {
        return new ResponseEntity<>(userUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<String> handleUserDeletionException(UserDeletionException userDeletionException) {
        return new ResponseEntity<>(userDeletionException.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
