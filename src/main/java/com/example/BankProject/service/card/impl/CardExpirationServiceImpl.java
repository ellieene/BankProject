package com.example.BankProject.service.card.impl;

import com.example.BankProject.model.entity.Card;
import com.example.BankProject.model.enums.Status;
import com.example.BankProject.repository.CardRepository;
import com.example.BankProject.service.card.CardExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardExpirationServiceImpl implements CardExpirationService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    @Scheduled(cron = "0 0 3 1 * *")
    public void deactivateExpiredCards() {
        LocalDate today = LocalDate.now();

        List<Card> expiredCards = cardRepository.findAllByStatusAndExpirationDateBefore(
                Status.ACTIVE,
                today
        );

        for (Card card : expiredCards) {
            card.setStatus(Status.EXPIRED);
        }

        if (!expiredCards.isEmpty()) {
            cardRepository.saveAll(expiredCards);
            log.info("Установлен статус {} для {} карт", Status.EXPIRED, expiredCards.size());
        }
    }
}