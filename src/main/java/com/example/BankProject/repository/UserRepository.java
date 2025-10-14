package com.example.BankProject.repository;

import com.example.BankProject.model.entity.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLoginOrEmail(String login, String email);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
}
