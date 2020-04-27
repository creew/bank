package com.example.bank.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.name());
        body.put("message", ex.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + " " + x.getDefaultMessage())
                .collect(Collectors.joining("\n")));
        body.put("path", request.getContextPath());
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({DuplicateEntryException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.CONFLICT);
        body.put("error", HttpStatus.CONFLICT.name());
        body.put("message", ex.getLocalizedMessage());
        body.put("path", request.getContextPath());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
