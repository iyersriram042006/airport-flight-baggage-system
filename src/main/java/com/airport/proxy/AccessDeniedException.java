package com.airport.proxy;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) { super(message); }
}
