package com.govt.irctc.exceptions.SecurityExceptions;

public class TokenNotFoundException extends Exception{
    public TokenNotFoundException(String message){
        super(message);
    }
}
