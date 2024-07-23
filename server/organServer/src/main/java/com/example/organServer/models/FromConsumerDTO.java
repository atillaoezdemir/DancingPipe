package com.example.organServer.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FromConsumerDTO {
    @NotNull(message = "KeyboardsMax cannot be null")
    private Integer keyboardsMax;

    @NotNull(message = "DefaultKeyboards cannot be null")
    private Integer defaultKeyboards;

    @NotNull(message = "BarLength cannot be null")
    private Integer barLength;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "ComposerName cannot be null")
    private String composerName;
}