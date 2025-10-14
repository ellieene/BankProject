package com.example.BankProject.exception;

/**
 * Исключение, обозначающее недостоверность учетных данных
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}