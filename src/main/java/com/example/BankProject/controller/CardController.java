package com.example.BankProject.controller;

import com.example.BankProject.model.dto.CardBlockResponseDTO;
import com.example.BankProject.model.dto.CardDTO;
import com.example.BankProject.model.dto.CheckDTO;
import com.example.BankProject.model.request.CardEditRequest;
import com.example.BankProject.model.request.CardCreateRequest;
import com.example.BankProject.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
@Tag(name = "Card")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Получить все карты пользователей (admin)")
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<CardDTO> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Создать новую карту (admin)")
    @PostMapping("/create")
    public ResponseEntity<String> createCard(@RequestBody CardCreateRequest request) {
        cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Карта успешно создана");
    }

    @Operation(summary = "Изменить карту (admin)")
    @PutMapping("/edit/{cardId}")
    public ResponseEntity<String> editCard(@PathVariable UUID cardId, @RequestBody CardEditRequest cardEditRequest) {
        cardService.editCard(cardId, cardEditRequest);
        return ResponseEntity.ok("Карта успешно изменена");
    }

    @Operation(summary = "Активировать карту (admin)")
    @PutMapping("/activate/{cardId}")
    public ResponseEntity<String> activateCard(@PathVariable UUID cardId) {
        cardService.activeCard(cardId);
        return ResponseEntity.ok("Карта успешно активирована");
    }

    @Operation(summary = "Удалить карту (admin)")
    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(summary = "Получить баланс карты (user/admin)")
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Long> getBalance(
            @PathVariable UUID cardId
    ) {
        long balance = cardService.getBalance(cardId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Перевод между своими картами (user/admin)")
    @PostMapping("/transfer")
    public ResponseEntity<CheckDTO> transfer(
            @RequestParam UUID fromCardId,
            @RequestParam UUID toCardId,
            @RequestParam long amount
    ) {
        return ResponseEntity.ok(cardService.transfer(fromCardId, toCardId, amount));
    }

    @Operation(summary = "Создать запрос на блокировку карты (user/admin)")
    @PostMapping("/block-request/{cardId}")
    public ResponseEntity<String> requestCardBlock(@PathVariable UUID cardId) {
        cardService.requestCardBlock(cardId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Запрос на блокировку создан");
    }

    @Operation(summary = "Получить все ожидающие обработки запросы на блокировку (admin)")
    @GetMapping("/block-requests/pending")
    public ResponseEntity<List<CardBlockResponseDTO>> getPendingBlockRequests() {
        List<CardBlockResponseDTO> requests = cardService.getPendingBlockRequests();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Обработать запрос на блокировку карты (admin)")
    @PutMapping("/block-requests/{requestId}")
    public ResponseEntity<String> processBlockRequest(
            @PathVariable UUID requestId,
            @RequestParam boolean approve
    ) {
        cardService.processBlockRequest(requestId, approve);
        return ResponseEntity.ok("Запрос успешно обработан");
    }

    @Operation(summary = "Поиск карт пользователя по последним 4 цифрам с пагинацией (user/admin)")
    @GetMapping("/search")
    public ResponseEntity<List<CardDTO>> searchCards(
            @RequestParam String last4Digits,
            @RequestParam int page,
            @RequestParam int size
    ) {
        List<CardDTO> cards = cardService.searchUserCardsByLast4Digits(last4Digits, page, size);
        return ResponseEntity.ok(cards);
    }
}