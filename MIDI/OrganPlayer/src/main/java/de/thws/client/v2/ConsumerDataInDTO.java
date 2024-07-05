package de.thws.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConsumerDataInDTO {
    @JsonProperty("keyboardsInUse")
    private int keyboardsInUse;

    @JsonProperty("command")
    private String command;

    @JsonProperty("currentTempo")
    private int currentTempo;

}
