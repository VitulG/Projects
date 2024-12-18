package com.govt.irctc.exceptions.SecurityExceptions;

public class PasswordMismatchException extends Exception{
    public PasswordMismatchException(String message){
        super(message);
    }
}
