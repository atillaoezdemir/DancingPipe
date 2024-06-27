package com.example.loginapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class WebClientDTO {
    private int keyboardsInUse;
    private int maxAvailableKeyboards;
}