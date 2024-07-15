package com.example.loginapp.enums;
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
            case 1 -> INCREMENT_KEYBOARDS;
            case 2 -> DECREMENT_KEYBOARDS;
            case 3 -> USE_ALL_KEYBOARDS;
            case 4 -> USE_ONE_KEYBOARD;
            case 5 -> INCREMENT_TEMPO;
            case 6 -> DECREMENT_TEMPO;
            case 7 -> DEFAULT_TEMPO;
            case 99 -> SEND_START_COMMAND;
            case 100 -> SEND_STOP_COMMAND;
            default -> UNDEFINED;
        };
    }
}