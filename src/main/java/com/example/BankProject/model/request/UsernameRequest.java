package com.example.BankProject.model.request;

import com.example.BankProject.config.annotation.ValidLoginAndEmail;

public record UsernameRequest(

    @ValidLoginAndEmail
    String username
){
}
