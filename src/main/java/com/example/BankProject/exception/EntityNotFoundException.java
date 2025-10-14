package com.example.BankProject.exception;

/**
 * Исключение, обозначающее что сущность не найдена в БД
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
