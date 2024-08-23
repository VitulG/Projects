package com.govt.irctc.advice.LoginAdvice;

public class TokenNotFoundException extends Exception{
    public TokenNotFoundException(String message){
        super(message);
    }
}
