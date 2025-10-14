package com.example.BankProject.exception;

/**
 * Исключение блокировки
 */
public class BlockedException extends RuntimeException {
    public BlockedException(String message) {
        super(message);
    }
}
