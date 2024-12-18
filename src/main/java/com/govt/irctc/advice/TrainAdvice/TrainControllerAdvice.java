package com.govt.irctc.advice.TrainAdvice;

import com.govt.irctc.exceptions.TrainExceptions.TrainCreationException;
import com.govt.irctc.exceptions.TrainExceptions.TrainDeletionException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainUpdationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TrainControllerAdvice {
    @ExceptionHandler(TrainCreationException.class)
    public ResponseEntity<String> handleTrainCreationException(TrainCreationException trainCreationException) {
        return new ResponseEntity<>(trainCreationException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception exception) {
        return new ResponseEntity<>("An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<String> handleTrainNotFoundException(TrainNotFoundException trainNotFoundException) {
        return new ResponseEntity<>(trainNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TrainUpdationException.class)
    public ResponseEntity<String> handleTrainUpdationException(TrainUpdationException trainUpdationException) {
        return new ResponseEntity<>(trainUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrainDeletionException.class)
    public ResponseEntity<String> handleTrainDeletionException(TrainDeletionException trainDeletionException) {
        return new ResponseEntity<>(trainDeletionException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
