package com.example.loginapp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ConnectionStatusToWebClientDTO {
    private final boolean producerConnected;
    private final boolean consumerConnected;
}
