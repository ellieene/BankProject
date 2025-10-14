package com.example.BankProject.model.dto;

import com.example.BankProject.model.enums.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardBlockResponseDTO {
    private String fio;
    private UUID id;
    private String number;
    private LocalDate expirationDate;
    private Status status;
}
