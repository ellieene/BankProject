package com.example.BankProject.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CardEditRequest(
        @Schema(description = "Срок действия", example = "2028-10-15")
        @NotBlank(message = "Срок действия карты не должнен быть пустым")
        LocalDate expirationDate,

        @Schema(description = "Баланс", example = "0")
        long balance
) {
}
