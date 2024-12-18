package com.govt.irctc.advice.SeatAdvice;

import com.govt.irctc.exceptions.SeatExceptions.SeatNotCreatedException;
import com.govt.irctc.exceptions.SeatExceptions.SeatTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SeatControllerAdvice {
    @ExceptionHandler(SeatNotCreatedException.class)
    public ResponseEntity<String> SeatNotCreatedException(SeatNotCreatedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SeatTypeException.class)
    public ResponseEntity<String> SeatTypeException(SeatTypeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> Exception(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
