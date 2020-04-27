package com.example.bank.exception;

import com.example.bank.dto.response.ErrorRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        ErrorRequestDTO error = new ErrorRequestDTO(status, ex.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + " " + x.getDefaultMessage())
                .collect(Collectors.joining("\n")),
                ((ServletWebRequest)request).getRequest().getRequestURI());
        return new ResponseEntity<>(error, headers, status);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DuplicateEntryException.class})
    protected ErrorRequestDTO handleConflict(RuntimeException ex, WebRequest request) {
        return new ErrorRequestDTO(HttpStatus.CONFLICT, ex.getLocalizedMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentsPassed.class})
    protected ErrorRequestDTO handleIllegalArguments(RuntimeException ex, WebRequest request) {
        return new ErrorRequestDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({IllegalCardIdPassed.class})
    protected ErrorRequestDTO handleIllegalCardIdPassed(RuntimeException ex, WebRequest request) {
        return new ErrorRequestDTO(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({WrongPasswordException.class})
    protected ErrorRequestDTO handleWrongPassword(RuntimeException ex, WebRequest request) {
        return new ErrorRequestDTO(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());
    }
}
