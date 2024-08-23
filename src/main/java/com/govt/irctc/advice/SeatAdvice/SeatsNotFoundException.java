package com.govt.irctc.advice.SeatAdvice;

public class SeatsNotFoundException extends Exception{
    public SeatsNotFoundException(String message){
        super(message);
    }
}
