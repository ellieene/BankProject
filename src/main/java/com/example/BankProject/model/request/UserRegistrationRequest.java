package com.example.BankProject.model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @Schema(description = "Email", example = "user1@example.com")
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @NotBlank(message = "Адрес электронной почты не может быть пустым")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        String email,

        @Schema(description = "Password", example = "password")
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password,

        @Schema(description = "Login", example = "user1")
        @Size(min = 4, max = 50, message = "Login должен содержать от 4 до 50 символов")
        @NotBlank(message = "Login не может быть пустым")
        String login,

        @Schema(description = "ФИО", example = "Иванов Иван Иванович")
        @NotBlank(message = "ФИО не должно быть пустым")
        @Pattern(
                regexp = "^[А-Яа-яЁё]+\\s[А-Яа-яЁё]+(\\s[А-Яа-яЁё]+)?$",
                message = "ФИО должно состоять из 2–3 слов, содержащих только русские буквы"
        )
        String fio
) {
}
