package com.example.BankProject.model.entity;

import com.example.BankProject.model.enums.Status;
import com.example.BankProject.util.converter.CryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "card")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 16)
    @Convert(converter = CryptoConverter.class) // Шифруем номер карты
    private String number;

    @Column(nullable = false, length = 5)
    private LocalDate expirationDate; // формат MM/yy

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getNumber() {
        if (number == null || number.length() < 4) {
            return "****";
        }
        String masked = number.replaceAll("\\d(?=\\d{4})", "*");
        return masked.replaceAll(".{4}(?=.)", "$0 ");
    }
}
