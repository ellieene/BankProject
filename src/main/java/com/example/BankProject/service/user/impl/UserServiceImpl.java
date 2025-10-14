package com.example.BankProject.service.user.impl;

import com.example.BankProject.exception.*;
import com.example.BankProject.model.entity.User;
import com.example.BankProject.model.enums.Role;
import com.example.BankProject.model.enums.Status;
import com.example.BankProject.model.request.UserEditRequest;
import com.example.BankProject.repository.UserRepository;
import com.example.BankProject.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.BankProject.util.CommonStrings.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void editUser(UUID userId, UserEditRequest request) {
        UUID currentAdminId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        if (isNotBlank(request.fio())) {
            user.setFio(request.fio().trim());
        }

        if (isNotBlank(request.email())) {
            updateEmailIfUnique(user, request.email().trim());
        }

        if (isNotBlank(request.login())) {
            updateLoginIfUnique(user, request.login().trim());
        }

        if (request.role() != null) {
            if (request.role() == Role.ADMIN) {
                if (user.getId().equals(currentAdminId)) {
                    throw new InvalidCredentialsException("Нельзя изменить роль самому себе на ADMIN");
                }
                user.setRole(Role.ADMIN);
            } else if (request.role() == Role.USER) {
                user.setRole(Role.USER);
            }
        }


        userRepository.save(user);
    }


    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        UUID currentAdminId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userId.equals(currentAdminId)) {
            throw new ForbiddenActionException(ADMIN_SELF_DELETE_FORBIDDEN);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        userRepository.delete(user);
    }


    @Override
    @Transactional
    public void blockUser(UUID userId) {
        UUID currentAdminId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userId.equals(currentAdminId)) {
            throw new ForbiddenActionException(ADMIN_SELF_DELETE_FORBIDDEN);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        if (user.getStatus() == Status.BLOCKED) {
            throw new BlockedException(USER_ALREADY_BLOCKED);
        }

        user.setStatus(Status.BLOCKED);
        userRepository.save(user);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private void updateEmailIfUnique(User user, String newEmail) {
        if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateDataException(EMAIL_IS_TAKEN);
        }
        user.setEmail(newEmail);
    }

    private void updateLoginIfUnique(User user, String newLogin) {
        if (!newLogin.equals(user.getLogin()) && userRepository.existsByLogin(newLogin)) {
            throw new DuplicateDataException(LOGIN_IS_TAKEN);
        }
        user.setLogin(newLogin);
    }
}
