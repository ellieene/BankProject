package com.example.BankProject.exception;

/**
 * Исключение, обозначающее что срок карты истек
 */
public class CardExpiredException extends RuntimeException {
    public CardExpiredException(String message) {
        super(message);
    }
}
