package com.example.BankProject.model.request;

import com.example.BankProject.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserEditRequest(

        @Schema(description = "ФИО", example = "Иванов Иван Иванович")
        @Pattern(
                regexp = "^[А-Яа-яЁё]+\\s[А-Яа-яЁё]+(\\s[А-Яа-яЁё]+)?$",
                message = "ФИО должно состоять из 2–3 слов, содержащих только русские буквы"
        )
        String fio,

        @Schema(description = "Email", example = "user1999@example.com")
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        String email,

        @Schema(description = "Login", example = "user3")
        @Size(min = 4, max = 50, message = "Login должен содержать от 4 до 50 символов")
        String login,

        @Schema(description = "Role", example = "ADMIN")
        Role role

) {
}
