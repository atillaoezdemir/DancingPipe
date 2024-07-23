package com.example.organServer.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserCredentialsDTO {
    private String username;
    private String password;
}
