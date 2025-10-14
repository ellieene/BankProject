package com.example.BankProject.exception;

/**
 * Исключение, для обозначения дублирующегося логина
 */
public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message) {
        super(message);
    }
}
