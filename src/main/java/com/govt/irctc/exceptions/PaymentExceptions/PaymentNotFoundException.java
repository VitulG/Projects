package com.govt.irctc.exceptions.PaymentExceptions;

public class PaymentNotFoundException extends Exception {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
