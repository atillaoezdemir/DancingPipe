package com.example.loginapp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserCredentialsDTO {
    private String username;
    private String password;
}
