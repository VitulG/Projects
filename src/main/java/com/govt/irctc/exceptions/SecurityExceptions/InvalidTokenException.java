package com.govt.irctc.exceptions.SecurityExceptions;


public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
