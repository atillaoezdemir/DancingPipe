package de.thws.configurators;

import lombok.Getter;

import java.io.Serializable;

/**
 * Represents the content of a JSON file, used for saving information about a composition.
 * <p>Class members:
 * <ul>
 *     <li> {@code compositionName} - title of the composition as {@link String}.
 *     <li> {@code cmposer} - composer of the composition as {@link String}.
 *     <li> {@code lengthInBars} - length of the composition in bars as {@code long}.
 *     <li> {@code tempoFactor} - the tempo factor, which is used when changing the tempo of the composition as {@code float}.
 * </ul>
 */
@Getter
public class CompositionConfigurator implements Serializable {
    private String compositionName;
    private String composer;
    private long lengthInBars;
    private float tempoFactor;

    CompositionConfigurator() {}

    public String toString() {
        return this.compositionName + " by " + this.composer + " (Length in bars: " + this.lengthInBars + ").";
    }
}
