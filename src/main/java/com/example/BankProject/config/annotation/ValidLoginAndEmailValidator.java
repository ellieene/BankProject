package com.example.BankProject.config.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidLoginAndEmailValidator implements ConstraintValidator<ValidLoginAndEmail, String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Логин/email не может быть пустым")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < 5 || value.length() > 255) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Логин/email должен быть от 5 до 255 символов")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
