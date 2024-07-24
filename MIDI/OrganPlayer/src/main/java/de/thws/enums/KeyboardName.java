package de.thws.enums;

import lombok.Getter;

@Getter
public enum KeyboardName {
    PEDAL(1, 4),
    CHOIR(2, 3),
    GREAT(3, 1),
    SWELL(4, 2),
    SOLO(5, 5);


    private final int channelNumber;
    private final int orderToPlay;

    KeyboardName(int channelNumber, int orderToPlay) {
        this.channelNumber = channelNumber;
        this.orderToPlay = orderToPlay;
    }

}
