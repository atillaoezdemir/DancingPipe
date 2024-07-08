package de.thws;

public enum Tempo {
    FAST(0.5f),
    FASTER(0.75f),
    NORMAL(1f),
    SLOWER(1.25f),
    SLOW(1.5f);

    private final float factor;

    Tempo(float factor) {
        this.factor = factor;
    }

    public float getValue() {
        return factor;
    }
}
