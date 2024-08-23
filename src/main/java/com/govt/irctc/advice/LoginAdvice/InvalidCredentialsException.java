package com.govt.irctc.advice.LoginAdvice;

public class InvalidCredentialsException extends Exception{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
