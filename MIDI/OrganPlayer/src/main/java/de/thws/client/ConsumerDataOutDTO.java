package de.thws.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ConsumerDataOutDTO {
    private int keyboardsMax;
    private int defaultKeyboards;
    private long barLength;
    private String title;
    private String composerName;

}