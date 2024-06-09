package com.swag.app.advice;

public class ContactNotFound extends Exception {
    public ContactNotFound(String message) {
        super(message);
    }
}
