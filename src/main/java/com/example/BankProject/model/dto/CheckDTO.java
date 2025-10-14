package com.example.BankProject.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckDTO {
    private String fromCard;
    private String toCard;
    private String date;
    private long transferAmount;
}
