package com.example.BankProject.service.auth.impl;

import com.example.BankProject.config.security.component.JwtTokenProvider;
import com.example.BankProject.exception.BlockedException;
import com.example.BankProject.exception.DuplicateDataException;
import com.example.BankProject.exception.EntityNotFoundException;
import com.example.BankProject.exception.InvalidCredentialsException;
import com.example.BankProject.model.entity.User;
import com.example.BankProject.model.enums.Role;
import com.example.BankProject.model.enums.Status;
import com.example.BankProject.model.request.UserAuthorizationRequest;
import com.example.BankProject.model.request.UserRegistrationRequest;
import com.example.BankProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRegistrationRequest userRegistrationRequest;
    private UserAuthorizationRequest userAuthorizationRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRegistrationRequest = new UserRegistrationRequest(
                "email@example.com",
                "123",
                "login",
                "Иванов Иван Иванович"
        );
        userAuthorizationRequest = new UserAuthorizationRequest(
                "user",
                "123"
        );
        user = new User();
    }

    @Test
    @DisplayName("Неуспешная регистрация, дублирование логина")
    void registration_fail_duplicate_login() {
        user.setLogin(userRegistrationRequest.login());

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));

        assertThrows(DuplicateDataException.class, () -> authService.registration(userRegistrationRequest));

        verify(userRepository).findByLoginOrEmail(userRegistrationRequest.login(), userRegistrationRequest.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Неуспешная регистрация, дублирование почты")
    void registration_fail_duplicate_email() {
        user.setLogin("test");
        user.setEmail(userRegistrationRequest.email());

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));

        assertThrows(DuplicateDataException.class, () -> authService.registration(userRegistrationRequest));

        verify(userRepository).findByLoginOrEmail(userRegistrationRequest.login(), userRegistrationRequest.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешная регистрация")
    void registration_access() {
        user.setLogin("test");
        user.setEmail("test");

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authService.registration(userRegistrationRequest));

        verify(userRepository).findByLoginOrEmail(userRegistrationRequest.login(), userRegistrationRequest.email());
        verify(userRepository).save(userArgumentCaptor.capture());
        assertEquals(Role.USER, userArgumentCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("Неуспешная аутентификация, пользователь не найден")
    void authorization_fail_login() {
        user.setLogin(userAuthorizationRequest.username());

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.authorization(userAuthorizationRequest));

        verify(userRepository).findByLoginOrEmail(eq(userAuthorizationRequest.username()), any());
    }

    @Test
    @DisplayName("Неуспешная аутентификация, неверный пароль")
    void authorization_fail_password() {
        user.setLogin("login");
        user.setPassword(userAuthorizationRequest.password());

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.authorization(userAuthorizationRequest));

        verify(userRepository).findByLoginOrEmail(any(), eq(userAuthorizationRequest.username()));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Неуспешная аутентификация, пользователь заблокирован")
    void authorization_fail_user_blocked() {
        user.setLogin("login");
        user.setPassword(userAuthorizationRequest.password());
        user.setStatus(Status.BLOCKED);

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThrows(BlockedException.class, () -> authService.authorization(userAuthorizationRequest));

        verify(userRepository).findByLoginOrEmail(any(), eq(userAuthorizationRequest.username()));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Успешная аутентификация")
    void authorization_success() {
        user.setLogin("login");
        user.setPassword(userAuthorizationRequest.password());
        user.setStatus(Status.ACTIVE);

        when(userRepository.findByLoginOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> authService.authorization(userAuthorizationRequest));

        verify(userRepository).findByLoginOrEmail(any(), eq(userAuthorizationRequest.username()));
        verify(jwtTokenProvider).generateToken(any());
    }
}
