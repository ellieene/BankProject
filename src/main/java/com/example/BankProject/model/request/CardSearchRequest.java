package com.example.BankProject.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CardSearchRequest(
        @Schema(description = "Последние 4 цифры карты", example = "1234")
        String last4Digits,

        @Schema(description = "Номер страницы (0-based)", example = "0")
        int page,

        @Schema(description = "Размер страницы", example = "10")
        int size,

        @Schema(description = "Поле для сортировки (например, createdAt или number)", example = "createdAt")
        String sortField,

        @Schema(description = "Направление сортировки (asc или desc)", example = "desc")
        String sortDir
) {}