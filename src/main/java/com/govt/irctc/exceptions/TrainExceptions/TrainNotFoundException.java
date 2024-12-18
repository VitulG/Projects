package com.govt.irctc.exceptions.TrainExceptions;

public class TrainNotFoundException extends Exception{
    public TrainNotFoundException(String message) {
        super(message);
    }
}
