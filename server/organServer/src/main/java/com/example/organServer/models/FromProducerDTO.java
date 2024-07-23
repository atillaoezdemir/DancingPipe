package com.example.organServer.models;


import javax.validation.constraints.NotNull;

public record FromProducerDTO(
        @NotNull
        Integer number
) {
}
