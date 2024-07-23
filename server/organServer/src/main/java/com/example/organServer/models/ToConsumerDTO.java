package com.example.organServer.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ToConsumerDTO {
    private Integer keyboardsInUse;
    private String command;
    private Integer currentTempo;
}
