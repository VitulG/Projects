package com.govt.irctc.advice.PaymentAdvice;

public class PaymentNotFoundException extends Exception {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
