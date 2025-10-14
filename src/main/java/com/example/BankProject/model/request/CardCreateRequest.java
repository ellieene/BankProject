package com.example.BankProject.model.request;

import com.example.BankProject.config.annotation.ValidLoginAndEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CardCreateRequest(
        @ValidLoginAndEmail
        @Schema(description = "Почта или логин", example = "email/login")
        @NotBlank(message = "Почта или логин, обязательны")
        String loginOrMail
) {
}
