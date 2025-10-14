package com.example.BankProject.model.dto;

import com.example.BankProject.model.enums.Role;
import lombok.Data;

@Data
public class UserDTO {

    private String login;
    private String email;
    private Role role;
    private int points;
}
