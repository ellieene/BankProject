package com.example.BankProject.repository;

import com.example.BankProject.model.entity.CardBlockRequest;
import com.example.BankProject.model.enums.BlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, UUID> {
    List<CardBlockRequest> findAllByStatus(BlockStatus status);
}
