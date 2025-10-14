package com.example.BankProject.service.card.impl;

import com.example.BankProject.exception.BlockedException;
import com.example.BankProject.exception.CardExpiredException;
import com.example.BankProject.exception.EntityNotFoundException;
import com.example.BankProject.exception.InsufficientFundsException;
import com.example.BankProject.model.dto.CardBlockResponseDTO;
import com.example.BankProject.model.dto.CardDTO;
import com.example.BankProject.model.dto.CheckDTO;
import com.example.BankProject.model.entity.Card;
import com.example.BankProject.model.entity.CardBlockRequest;
import com.example.BankProject.model.entity.User;
import com.example.BankProject.model.enums.BlockStatus;
import com.example.BankProject.model.enums.Status;
import com.example.BankProject.model.request.CardEditRequest;
import com.example.BankProject.model.request.CardCreateRequest;
import com.example.BankProject.repository.CardBlockRequestRepository;
import com.example.BankProject.repository.CardRepository;
import com.example.BankProject.repository.UserRepository;
import com.example.BankProject.service.card.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.example.BankProject.util.CommonStrings.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final CardBlockRequestRepository cardBlockRequestRepository;

    private static final long CARD_EXPIRATION_DATE = 3;
    private static final Random RANDOM = new Random();

    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> getAllCards() {
        List<Card> cards = cardRepository.findAll();

        log.info("Получено {} карт", cards.size());
        return modelMapper.map(cards, new TypeToken<List<CardDTO>>() {}.getType());
    }

    @Override
    @Transactional
    public void createCard(CardCreateRequest cardCreateRequest) {
        User user = userRepository.findByLoginOrEmail(cardCreateRequest.loginOrMail(), cardCreateRequest.loginOrMail())
                .orElseThrow(() -> new EntityNotFoundException(UNCORRECT_EMAIL_AND_LOGIN));

        String cardNumber = generateUniqueCardNumber();

        LocalDate expirationDate = LocalDate.now().plusYears(CARD_EXPIRATION_DATE);

        Card newCard = Card.builder()
                .user(user)
                .number(cardNumber)
                .expirationDate(expirationDate)
                .status(Status.ACTIVE)
                .build();

        cardRepository.save(newCard);
        log.info("Создана банковская карта с id {}", newCard.getId());
    }

    @Override
    @Transactional
    public void editCard(UUID cardId, CardEditRequest cardEditRequest) {
        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException(UNCORRECT_NUMBER));

        card.setExpirationDate(cardEditRequest.expirationDate());
        card.setBalance(cardEditRequest.balance());

        cardRepository.save(card);
        log.info("Изменена банковская карта с id {}", card.getId());
    }

    @Override
    @Transactional
    public void deleteCard(UUID cardId) {
        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException(UNCORRECT_NUMBER));

        cardRepository.delete(card);
        log.info("Удалена банковская карта с id {}", card.getId());
    }

    @Override
    @Transactional
    public void activeCard(UUID cardId) {
        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException(UNCORRECT_NUMBER));

        card.setStatus(Status.ACTIVE);
        cardRepository.save(card);
        log.info("Активирована банковская карта с id {}", card.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getBalance(UUID cardId) {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Card card = getCardByIdOrThrow(cardId, userId);

        log.info("Баланс банковской карта с id {} : {}", card.getId(), card.getBalance());
        return card.getBalance();
    }

    @Override
    @Transactional
    public CheckDTO transfer(UUID fromCardId, UUID toCardId, long amount) {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Card fromCard = getCardByIdOrThrow(fromCardId, userId);
        Card toCard = getCardByIdOrThrow(toCardId, userId);

        if (fromCard.getBalance() < amount) {
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS);
        }

        checkCardStatus(fromCard.getStatus());
        checkCardStatus(toCard.getStatus());

        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("Переведено {} из карты номер {} на {}", amount, fromCard.getNumber(), toCard.getNumber());
        return CheckDTO.builder()
                .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")))
                .fromCard(fromCard.getNumber())
                .toCard(toCard.getNumber())
                .transferAmount(amount)
                .build();
    }

    @Override
    @Transactional
    public void requestCardBlock(UUID cardId) {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Card card = getCardByIdOrThrow(cardId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        CardBlockRequest request = CardBlockRequest.builder()
                .card(card)
                .requestedBy(user)
                .status(BlockStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        cardBlockRequestRepository.save(request);
        log.info("Запрос на блокировку карты с id {}", cardId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardBlockResponseDTO> getPendingBlockRequests() {
        List<CardBlockRequest> pendingRequests = cardBlockRequestRepository.findAllByStatus(BlockStatus.PENDING);

        log.info("Получено {} запросов на блокировку", pendingRequests.size());
        return modelMapper.map(pendingRequests, new TypeToken<List<CardBlockResponseDTO>>() {}.getType());
    }

    @Override
    @Transactional
    public void processBlockRequest(UUID requestId, boolean approve) {
        CardBlockRequest request = cardBlockRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND));

        if (approve) {
            request.getCard().setStatus(Status.BLOCKED);
            cardRepository.save(request.getCard());
            request.setStatus(BlockStatus.APPROVED);
        } else {
            request.setStatus(BlockStatus.REJECTED);
        }

        request.setProcessedAt(LocalDateTime.now());
        cardBlockRequestRepository.save(request);
        log.info("Статус запроса с id {} обнавлен на {}", requestId, request.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> searchUserCardsByLast4Digits(String lastDigits, int page, int size) {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        List<CardDTO> filteredCards = cardRepository.findAllByUserId(userId).stream()
                .filter(card -> {
                    String cardNumber = card.getNumber();
                    String lastFour = cardNumber.substring(cardNumber.length() - 4);
                    return lastFour.contains(lastDigits);
                })
                .map(card -> modelMapper.map(card, CardDTO.class))
                .sorted(Comparator.comparing(CardDTO::getExpirationDate))
                .toList();

        int start = page * size;
        return getSubList(filteredCards, start, start + size);
    }

    private String generateUniqueCardNumber() {
        String number;
        do {
            long part1 = Math.abs(RANDOM.nextLong()) % 1_0000_0000_0000_0000L; // 16 цифр
            number = String.format("%016d", part1);
        } while (cardRepository.existsByNumber(number));

        return number;
    }

    private Card getCardByIdOrThrow(UUID cardId, UUID userId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_OR_NOT_BELONG_USER));
    }

    private void checkCardStatus(Status status) {
        if (Status.BLOCKED.equals(status)) {
            throw new BlockedException(CARD_BLOCKED);
        }

        if (Status.EXPIRED.equals(status)) {
            throw new CardExpiredException(CARD_EXPIRED);
        }
    }

    private List<CardDTO> getSubList(List<CardDTO> cards, int from, int to) {
        if (from >= cards.size()) {
            return List.of();
        }

        int end = Math.min(to, cards.size());
        return cards.subList(from, end);
    }
}
