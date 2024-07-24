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

    private final int factor;


    Tempo(int factor) {
        this.factor = factor;
    }

    public int getValue() {
        return this.factor;
    }
}
