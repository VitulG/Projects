package com.govt.irctc.advice.SeatAdvice;

public class SeatNotCreatedException extends Exception{
    public SeatNotCreatedException(String message) {
        super(message);
    }
}
