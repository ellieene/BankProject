package com.example.BankProject.repository;

import com.example.BankProject.model.entity.Card;
import com.example.BankProject.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findAllByUserId(UUID userId);
    Optional<Card> findByIdAndUserId(UUID cardId, UUID userId);
    Optional<Card> findCardById(UUID cardId);
    boolean existsByNumber(String number);
    List<Card> findAllByStatusAndExpirationDateBefore(Status status, LocalDate date);
}
