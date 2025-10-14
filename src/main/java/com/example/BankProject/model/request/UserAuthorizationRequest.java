package com.example.BankProject.model.request;

import com.example.BankProject.config.annotation.ValidLoginAndEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthorizationRequest(

        @ValidLoginAndEmail
        @Schema(description = "Почта или логин", example = "email/login")
        String username,

        @Schema(description = "Password", example = "password")
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}

