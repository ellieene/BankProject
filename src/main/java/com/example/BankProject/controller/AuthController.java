package com.example.BankProject.controller;

import com.example.BankProject.model.request.UserAuthorizationRequest;
import com.example.BankProject.model.request.UserRegistrationRequest;
import com.example.BankProject.service.auth.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @Operation(summary = "Registration")
    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        authServiceImpl.registration(userRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
    }

    @Operation(summary = "Authorization")
    @PostMapping("/authorization")
    public ResponseEntity<String> authorization(@Valid @RequestBody UserAuthorizationRequest userAuthorizationResponse) {
        return ResponseEntity.ok(authServiceImpl.authorization(userAuthorizationResponse));
    }
}
