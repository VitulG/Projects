package com.govt.irctc.exceptions.SecurityExceptions;

public class InvalidCredentialsException extends Exception{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
