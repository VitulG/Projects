package com.social.connectify.exceptions;

public class SessionAlreadyActiveException extends Exception {
    public SessionAlreadyActiveException(String message) {
        super(message);
    }
}
