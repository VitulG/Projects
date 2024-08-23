package com.govt.irctc.advice.TrainAdvice;

public class TrainNotFoundException extends Exception{
    public TrainNotFoundException(String message) {
        super(message);
    }
}
