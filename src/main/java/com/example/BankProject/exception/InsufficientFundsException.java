package com.example.BankProject.exception;

/**
 * Исключение, обозначающее недостаток средств
 */
public class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String message) {super(message);}
}
