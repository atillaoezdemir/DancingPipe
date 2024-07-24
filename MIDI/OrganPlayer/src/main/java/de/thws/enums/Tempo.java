package de.thws.enums;

/**
 * Enum for tempo.
 */
public enum Tempo {
    VERY_FAST(-2),
    FAST(-1),
    NORMAL(0),
    SLOW(1),
    VERY_SLOW(2);

    private final int adjustment;


    Tempo(int adjustment) {
        this.adjustment = adjustment;
    }

    public int getValue() {
        return this.adjustment;
    }
}
