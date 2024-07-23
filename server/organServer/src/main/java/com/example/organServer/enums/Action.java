package com.example.organServer.enums;
public enum Action {
    INCREMENT_KEYBOARDS,
    DECREMENT_KEYBOARDS,
    USE_ALL_KEYBOARDS,
    USE_ONE_KEYBOARD,
    INCREMENT_TEMPO,
    DECREMENT_TEMPO,
    DEFAULT_TEMPO,
    SEND_START_COMMAND,
    SEND_STOP_COMMAND,
    UNDEFINED;

    public static Action getAction(int number) {
        return switch (number) {
            case 11 -> INCREMENT_KEYBOARDS;
            case 6 -> DECREMENT_KEYBOARDS;
            case 21 -> USE_ALL_KEYBOARDS;
            case 16 -> USE_ONE_KEYBOARD;
            case 3 -> INCREMENT_TEMPO;
            case 2 -> DECREMENT_TEMPO;
            case 5 -> DEFAULT_TEMPO;
            case 0 -> SEND_START_COMMAND;
            case 26 -> SEND_STOP_COMMAND;
            default -> UNDEFINED;
        };
    }
}