package com.example.demo.domain.exceptions;

public class CriticalErrorException extends RuntimeException{
    public CriticalErrorException(String message) {
        super(message);
    }
}
