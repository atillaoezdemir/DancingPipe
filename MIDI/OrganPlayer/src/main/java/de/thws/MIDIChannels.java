package de.thws;

public enum MIDIChannels {
    CHOIR(1), GREAT(2), SWELL(3), SOLO(4), PEDALS(5);

    MIDIChannels(int channelNumber) {}

    int getChannelNumber() {
        return ordinal();
    }
}
