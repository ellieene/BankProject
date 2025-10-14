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
import com.example.BankProject.model.request.CardCreateRequest;
import com.example.BankProject.model.request.CardEditRequest;
import com.example.BankProject.repository.CardBlockRequestRepository;
import com.example.BankProject.repository.CardRepository;
import com.example.BankProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;
    @InjectMocks
    private CardServiceImpl cardService;
    @Captor
    private ArgumentCaptor<Card> cardArgumentCaptor;
    @Captor
    private ArgumentCaptor<CardBlockRequest> cardBlockRequestArgumentCaptor;
    private Card card;
    private User user;
    private static final UUID uuid = UUID.randomUUID();
    private static final long CARD_EXPIRATION_DATE = 3;

    @BeforeEach
    void setUp() {
        card = Card.builder()
                .number("01234")
                .balance(123L)
                .status(Status.ACTIVE)
                .build();
        user = User.builder().build();
    }

    @Test
    @DisplayName("Получить список банковских карт")
    void getAllCards() {
        when(modelMapper.map(any(), eq(new TypeToken<List<CardDTO>>() {}.getType()))).thenReturn(List.of(new CardDTO()));

        List<CardDTO> result = cardService.getAllCards();

        assertThat(result).isNotEmpty();
        verify(cardRepository).findAll();
    }

    @Test
    @DisplayName("Успешное создание банковской карты")
    void createCard() {
        CardCreateRequest createRequest = new CardCreateRequest("login");

        when(userRepository.findByLoginOrEmail(createRequest.loginOrMail(), createRequest.loginOrMail()))
                .thenReturn(Optional.of(user));
        when(cardRepository.existsByNumber(any())).thenReturn(false);

        cardService.createCard(createRequest);

        verify(cardRepository).save(cardArgumentCaptor.capture());
        assertEquals(user, cardArgumentCaptor.getValue().getUser());
        assertNotNull(cardArgumentCaptor.getValue().getNumber());
        assertEquals(LocalDate.now().plusYears(CARD_EXPIRATION_DATE), cardArgumentCaptor.getValue().getExpirationDate());
        assertEquals(Status.ACTIVE, cardArgumentCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Не успешное создание банковской карты")
    void createCard_fail() {
        CardCreateRequest createRequest = new CardCreateRequest("login");

        when(userRepository.findByLoginOrEmail(createRequest.loginOrMail(), createRequest.loginOrMail()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(createRequest));

        verify(cardRepository, never()).existsByNumber(any());
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное изменение срока действиия карты")
    void editCard() {
        CardEditRequest editRequest = new CardEditRequest(LocalDate.MAX, 123L);

        when(cardRepository.findCardById(uuid)).thenReturn(Optional.of(card));

        cardService.editCard(uuid, editRequest);

        verify(cardRepository).save(cardArgumentCaptor.capture());
        assertEquals(editRequest.expirationDate(), cardArgumentCaptor.getValue().getExpirationDate());
        assertEquals(editRequest.balance(), cardArgumentCaptor.getValue().getBalance());
    }

    @Test
    @DisplayName("Не успешное изменение срока действиия карты")
    void editCard_fail() {
        CardEditRequest editRequest = new CardEditRequest(LocalDate.MAX, 123L);

        when(cardRepository.findCardById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.editCard(uuid, editRequest));

        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное удаление срока действиия карты")
    void deleteCard() {
        when(cardRepository.findCardById(uuid)).thenReturn(Optional.of(card));

        cardService.deleteCard(uuid);

        verify(cardRepository).delete(card);
    }

    @Test
    @DisplayName("Не успешное удаление срока действиия карты")
    void deleteCard_fail() {
        when(cardRepository.findCardById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.deleteCard(uuid));

        verify(cardRepository, never()).delete(card);
    }

    @Test
    @DisplayName("Успешная активация карты")
    void activeCard() {
        when(cardRepository.findCardById(uuid)).thenReturn(Optional.of(card));

        cardService.activeCard(uuid);

        verify(cardRepository).save(cardArgumentCaptor.capture());
        assertEquals(Status.ACTIVE, cardArgumentCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Не успешная активация карты")
    void activeCard_fail() {
        when(cardRepository.findCardById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.activeCard(uuid));

        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное получение баланса карты")
    void getBalance() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {

            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card));

            long result = cardService.getBalance(uuid);

            verify(cardRepository).findByIdAndUserId(uuid, uuid);
            assertEquals(card.getBalance(), result);
            mockedHolder.verify(SecurityContextHolder::getContext);
        }
    }

    @Test
    @DisplayName("Не успешное получение баланса карты")
    void getBalance_fail() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {

            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> cardService.getBalance(uuid));

            mockedHolder.verify(SecurityContextHolder::getContext);
        }
    }

    @Test
    @DisplayName("Успешный перевод средств между картами")
    void transfer() {
        LocalDateTime fixedTime = LocalDateTime.of(2023, 12, 25, 14, 30);
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class);
                MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);
            Card anotherCard = Card.builder()
                    .number("43210")
                    .status(Status.ACTIVE)
                    .build();
            long amount = 100L;

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card))
                    .thenReturn(Optional.of(anotherCard));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedTime);

            CheckDTO result = cardService.transfer(uuid, uuid, amount);

            assertEquals(23L, card.getBalance());
            assertEquals(amount, anotherCard.getBalance());
            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardRepository, times(2)).save(any());
            assertEquals("25/12/23 14:30", result.getDate());
            assertEquals(card.getNumber(), result.getFromCard());
            assertEquals(anotherCard.getNumber(), result.getToCard());
            assertEquals(amount, result.getTransferAmount());
        }
    }

    @Test
    @DisplayName("Не успешный перевод средств между картами, карта не найдена")
    void transfer_fail_card_not_found() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);
            long amount = 100L;

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> cardService.transfer(uuid, uuid, amount));

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Не успешный перевод средств между картами, не хватает средств для перевода")
    void transfer_fail_too_less_balance() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);
            Card anotherCard = Card.builder().build();
            card.setBalance(99L);
            long amount = 100L;

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card))
                    .thenReturn(Optional.of(anotherCard));

            assertThrows(InsufficientFundsException.class, () -> cardService.transfer(uuid, uuid, amount));

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardRepository, never()).save(any());
        }
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"BLOCKED", "EXPIRED"})
    @DisplayName("Не успешный перевод средств между картами, неверные статусы карты")
    void transfer_fail_wrong_card_status(Status status) {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);
            Card anotherCard = Card.builder()
                    .number("43210")
                    .status(status)
                    .build();
            long amount = 100L;

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card))
                    .thenReturn(Optional.of(anotherCard));

            switch (status) {
                case BLOCKED -> assertThrows(BlockedException.class, () -> cardService.transfer(uuid, uuid, amount));
                case EXPIRED -> assertThrows(CardExpiredException.class, () -> cardService.transfer(uuid, uuid, amount));
            }

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Успешное создание запроса на блокировку карты")
    void requestCardBlock() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card));
            when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

            cardService.requestCardBlock(uuid);

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardBlockRequestRepository).save(cardBlockRequestArgumentCaptor.capture());
            assertEquals(card, cardBlockRequestArgumentCaptor.getValue().getCard());
            assertEquals(user, cardBlockRequestArgumentCaptor.getValue().getRequestedBy());
            assertEquals(BlockStatus.PENDING, cardBlockRequestArgumentCaptor.getValue().getStatus());
            assertNotNull(cardBlockRequestArgumentCaptor.getValue().getCreatedAt());
        }
    }

    @Test
    @DisplayName("Не успешное создание запроса на блокировку карты, карта не найдена")
    void requestCardBlock_card_not_fount() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> cardService.requestCardBlock(uuid));

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardBlockRequestRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Не успешное создание запроса на блокировку карты, пользователь не найдена")
    void requestCardBlock_user_not_fount() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findByIdAndUserId(uuid, uuid)).thenReturn(Optional.of(card));
            when(userRepository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> cardService.requestCardBlock(uuid));

            mockedHolder.verify(SecurityContextHolder::getContext);
            verify(cardBlockRequestRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Успешное получение ожидающих обработки запросов на блокировку")
    void getPendingBlockRequests() {
        when(modelMapper.map(anyList(), eq(new TypeToken<List<CardBlockResponseDTO>>() {}.getType())))
                .thenReturn(List.of(new CardBlockResponseDTO()));

        List<CardBlockResponseDTO> result = cardService.getPendingBlockRequests();

        assertThat(result).isNotEmpty();
        verify(cardBlockRequestRepository).findAllByStatus(BlockStatus.PENDING);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Успешная обработка запроса на блокировку")
    void processBlockRequest(boolean approve) {
        CardBlockRequest request = new CardBlockRequest();
        request.setCard(new Card());

        when(cardBlockRequestRepository.findById(uuid)).thenReturn(Optional.of(request));

        cardService.processBlockRequest(uuid, approve);

        if (approve) {
            assertEquals(Status.BLOCKED, request.getCard().getStatus());
            verify(cardRepository).save(request.getCard());
            assertEquals(BlockStatus.APPROVED, request.getStatus());
        } else {
            assertEquals(BlockStatus.REJECTED, request.getStatus());
        }

        assertNotNull(request.getProcessedAt());
        verify(cardBlockRequestRepository).save(request);
    }

    @Test
    @DisplayName("Не успешная обработка запроса на блокировку")
    void processBlockRequest_fail() {
        CardBlockRequest request = new CardBlockRequest();
        request.setCard(new Card());

        when(cardBlockRequestRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.processBlockRequest(uuid, true));

        verify(cardRepository, never()).save(any());
        verify(cardBlockRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешный поиск карт пользователя")
    void searchUserCardsByLast4Digits() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = Mockito.mock(SecurityContext.class);
            Authentication auth = Mockito.mock(Authentication.class);
            Card anotherCard = Card.builder()
                    .number("00000")
                    .build();

            when(auth.getPrincipal()).thenReturn(uuid.toString());
            when(context.getAuthentication()).thenReturn(auth);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(context);
            when(cardRepository.findAllByUserId(uuid)).thenReturn(List.of(card, anotherCard));
            when(modelMapper.map(eq(card), any())).thenReturn(new CardDTO());

            List<CardDTO> cards = cardService.searchUserCardsByLast4Digits("3", 0, 2);

            mockedHolder.verify(SecurityContextHolder::getContext);
            assertThat(cards)
                    .isNotEmpty()
                    .hasSize(1);
        }
    }
}