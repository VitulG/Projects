package com.govt.irctc.advice.LoginAdvice;


public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
