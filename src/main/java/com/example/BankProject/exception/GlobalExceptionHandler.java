package com.example.BankProject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenActionException.class)
    public String handleException(ForbiddenActionException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    @ExceptionHandler(InsufficientFundsException.class)
    public String handleException(InsufficientFundsException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(BlockedException.class)
    public String handleException(BlockedException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(CardExpiredException.class)
    public String handleException(CardExpiredException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateDataException.class)
    public String handleException(DuplicateDataException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleException(InvalidCredentialsException e) {
        return e.getMessage();
    }

}