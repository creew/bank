package com.example.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgumentsPassed extends RuntimeException{
    public IllegalArgumentsPassed(String message) {
        super(message);
    }
}
