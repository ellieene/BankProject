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
import com.example.BankProject.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.BankProject.util.CommonStrings.*;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public void registration(UserRegistrationRequest userRegistrationRequest) {
        userRepository.findByLoginOrEmail(userRegistrationRequest.login(), userRegistrationRequest.email()).ifPresent(user -> {
            if (user.getLogin().equals(userRegistrationRequest.login())) {
                throw new DuplicateDataException(LOGIN_IS_TAKEN);
            }
            if (user.getEmail().equals(userRegistrationRequest.email())) {
                throw new DuplicateDataException(EMAIL_IS_TAKEN);
            }
        });
        User newUser = User.builder()
                .email(userRegistrationRequest.email())
                .password(passwordEncoder.encode(userRegistrationRequest.password()))
                .login(userRegistrationRequest.login())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .fio(userRegistrationRequest.fio())
                .build();
        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    @Override
    public String authorization(UserAuthorizationRequest userAuthorizationRequest) {
        User user = userRepository
                .findByLoginOrEmail(userAuthorizationRequest.username(), userAuthorizationRequest.username())
                .orElseThrow(() -> new EntityNotFoundException(UNCORRECT_EMAIL_AND_LOGIN));
        if (!passwordEncoder.matches(userAuthorizationRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException(UNCORRECT_PASSWORD);
        }
        if (user.getStatus() == Status.BLOCKED) {
            throw new BlockedException(USER_BLOCKED);
        }
        return jwtTokenProvider.generateToken(user);
    }
}
