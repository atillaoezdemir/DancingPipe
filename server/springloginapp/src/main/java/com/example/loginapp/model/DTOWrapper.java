package com.example.loginapp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class DTOWrapper {
    private final ToConsumerDTO toConsumerDTO;
    private final ToWebClientDTO toWebClientDTO;
}
