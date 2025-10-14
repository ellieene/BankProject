package com.example.BankProject.service.card;

import com.example.BankProject.model.dto.CardBlockResponseDTO;
import com.example.BankProject.model.dto.CardDTO;
import com.example.BankProject.model.dto.CheckDTO;
import com.example.BankProject.model.request.CardEditRequest;
import com.example.BankProject.model.request.CardCreateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с банковскими картами.
 * Содержит методы для создания, удаления, активации,
 * перевода средств, просмотра баланса и обработки блокировок.
 */
public interface CardService {

    /**
     * Получить список банковских карт.
     *
     * @return список {@link CardDTO}
     */
    List<CardDTO> getAllCards();

    /**
     * Создание банковской карты.
     *
     * @param cardCreateRequest DTO с данными для создания карты
     */
    void createCard(CardCreateRequest cardCreateRequest);

    /**
     * Изменение срока действиия карты.
     *
     * @param cardId UUID карты
     * @param cardEditRequest DTO с данными для изменения карты
     */
    void editCard(UUID cardId, CardEditRequest cardEditRequest);

    /**
     * Активировать банковскую карту.
     *
     * @param cardId UUID карты
     */
    void activeCard(UUID cardId);

    /**
     * Удалить банковскую карту.
     *
     * @param cardId UUID карты
     */
    void deleteCard(UUID cardId);

    /**
     * Получить баланс карты конкретного пользователя.
     *
     * @param cardId UUID карты
     * @return баланс карты
     */
    long getBalance(UUID cardId);

    /**
     * Перевести средства между картами одного пользователя.
     *
     * @param fromCardId UUID карты отправителя
     * @param toCardId UUID карты получателя
     * @param amount сумма перевода
     * @return {@link CheckDTO} чек перевода
     */
    CheckDTO transfer(UUID fromCardId, UUID toCardId, long amount);

    /**
     * Создать запрос на блокировку карты пользователя.
     *
     * @param cardId UUID карты
     */
    void requestCardBlock(UUID cardId);

    /**
     * Получить все ожидающие обработки запросы на блокировку.
     *
     * @return {@link CardBlockResponseDTO} список запросов на блокировку со статусом PENDING
     */
    List<CardBlockResponseDTO> getPendingBlockRequests();

    /**
     * Обработать запрос на блокировку карты.
     *
     * @param requestId UUID запроса
     * @param approve true, если запрос одобрен; false, если отклонён
     */
    void processBlockRequest(UUID requestId, boolean approve);

    /**
     * Поиск карт пользователя по последним 4 цифрам с поддержкой пагинации.
     * Номера карт хранятся в зашифрованном виде, расшифровка происходит автоматически.
     *
     * @param last4Digits строка для поиска
     * @param page страницы
     * @param size размер страницы
     * @return {@link CardDTO} страница DTO карт, удовлетворяющих условию
     */
    List<CardDTO> searchUserCardsByLast4Digits(String last4Digits, int page, int size);

}