package com.govt.irctc.advice.BookingAdvice;

public class BookingNotFoundException extends Exception{
    public BookingNotFoundException(String message) {
        super(message);
    }
}
