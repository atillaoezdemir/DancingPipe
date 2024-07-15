package com.example.loginapp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class ToConsumerDTO {
    private int keyboardsInUse;
    private String command;
    private int currentTempo;
}
