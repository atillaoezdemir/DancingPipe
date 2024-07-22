package de.thws.enums;

public enum KeyboardName {
    PEDAL(1),
    CHOIR(2),
    GREAT(3),
    SWELL(4),
    SOLO(5);

    KeyboardName(int channelNumber) {}

    public int getChannelNumber() {
        return ordinal();
    }
}
