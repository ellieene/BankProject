package com.example.BankProject.model.dto;

import com.example.BankProject.model.enums.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class CardDTO {
    private UUID id;
    private String number;
    private String fio;
    private String expirationDate;
    private long balance;
    private Status status;

}
