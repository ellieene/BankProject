package com.example.BankProject.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration on the PasswordEncoderConfig
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Создает и возвращает кодировщик паролей для хеширования.
     * <p>
     * Используется алгоритм BCrypt с силой хеширования по умолчанию (10).
     *
     * @return реализацию {@link PasswordEncoder} на основе BCrypt
     * @see BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
