package com.example.BankProject.controller;

import com.example.BankProject.model.request.UserEditRequest;
import com.example.BankProject.service.user.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-management")
@Tag(name = "User management")
public class UserManagementController {

    private final UserServiceImpl userService;

    @PutMapping("/{userId}")
    @Operation(summary = "Изменение пользователя")
    public ResponseEntity<String> editUser(@PathVariable UUID userId, @RequestBody UserEditRequest userEditRequest) {
        userService.editUser(userId, userEditRequest);
        return ResponseEntity.ok("Пользователь изменен");
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Заблокировать пользователя")
    @PutMapping("/blocke-user/{userId}")
    public ResponseEntity<String> blockedUser(@PathVariable UUID userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok("Пользователь заблокирован");
    }
}
