package com.example.BankProject.service.card;

/**
 * Сервис для проверки актуальности карт
 */
public interface CardExpirationService {

    /**
     * Проверяет карты, срок действия которых истёк
     * запускается 1-го числа каждого месяца в 03:00 ночи
     */
    void deactivateExpiredCards();
}
