package com.example.demo.domain.exceptions;

public class ErrorException extends RuntimeException {
    public ErrorException(String message) {
        super(message);
    }
}
