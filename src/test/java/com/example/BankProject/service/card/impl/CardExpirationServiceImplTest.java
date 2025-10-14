package com.example.BankProject.service.card.impl;

import com.example.BankProject.model.entity.Card;
import com.example.BankProject.model.enums.Status;
import com.example.BankProject.repository.CardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardExpirationServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private CardExpirationServiceImpl cardExpirationService;

    @Test
    @DisplayName("Успешная деактивация карт")
    void deactivateExpiredCards() {
        List<Card> cards = List.of(new Card(), new Card());

        when(cardRepository.findAllByStatusAndExpirationDateBefore(eq(Status.ACTIVE), any())).thenReturn(cards);

        assertDoesNotThrow(() -> cardExpirationService.deactivateExpiredCards());
        assertThat(cards).allMatch(card -> card.getStatus().equals(Status.EXPIRED));
        verify(cardRepository).saveAll(cards);
    }

    @Test
    @DisplayName("Успешная деактивация карт, пустой список")
    void deactivateExpiredCards_empty_list() {
        List<Card> cards = List.of();

        when(cardRepository.findAllByStatusAndExpirationDateBefore(eq(Status.ACTIVE), any())).thenReturn(cards);

        assertDoesNotThrow(() -> cardExpirationService.deactivateExpiredCards());
        verify(cardRepository, never()).saveAll(cards);
    }
}