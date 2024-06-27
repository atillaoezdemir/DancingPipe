package com.example.loginapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FromConsumerDTO {
    @JsonProperty("keyboardsMax")
    public int keyboardsMax;
    @JsonProperty("defaultKeyboards")
    private int defaultKeyboards;
}