package com.govt.irctc.advice;

public class UserBookingsNotFoundException extends Exception {
    public UserBookingsNotFoundException(String message) {
        super(message);
    }
}
