package com.example.BankProject.exception;

/**
 * Исключение, обозначающее что доступ запрещен
 */
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}