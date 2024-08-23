package com.govt.irctc.advice.LoginAdvice;

public class PasswordMismatchException extends Exception{
    public PasswordMismatchException(String message){
        super(message);
    }
}
