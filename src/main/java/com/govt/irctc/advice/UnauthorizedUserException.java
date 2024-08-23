package com.govt.irctc.advice;

public class UnauthorizedUserException extends Exception{
    public UnauthorizedUserException(String message){
        super(message);
    }
}
