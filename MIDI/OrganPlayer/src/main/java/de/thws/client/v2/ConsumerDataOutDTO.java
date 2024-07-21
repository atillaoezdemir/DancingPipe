package de.thws.client.v2;

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
    private String compositionTitle;
    private String composerName;

}