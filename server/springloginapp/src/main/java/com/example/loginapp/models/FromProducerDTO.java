package com.example.loginapp.models;


import javax.validation.constraints.NotNull;

public record FromProducerDTO(
        @NotNull
        Integer number
) {
}
