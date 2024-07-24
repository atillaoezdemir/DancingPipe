package de.thws.configurators;
import lombok.Getter;

import java.io.Serializable;

/**
 * Represents the content of a JSON file, used for saving information about a keyboard.
 * <p>Class members:
 * <ul>
 *     <li> {@code keyboardName} - name of the keyboard as {@link String} (in sense of {@link de.thws.enums.KeyboardName}).
 *     <li> {@code patternConfigurators} - array of {@link PatternConfigurator} objects. It describes the configuration of the single patterns
 * </ul>
 * @see PatternConfigurator
 * @see de.thws.enums.KeyboardName
 */
@Getter
public class KeyboardConfigurator implements Serializable {
    String keyboardName;
    PatternConfigurator[] patternConfigurators;

    public KeyboardConfigurator() {}

    public KeyboardConfigurator(String keyboardName, PatternConfigurator[] patternConfigurators) {
        this.keyboardName = keyboardName;
        this.patternConfigurators = patternConfigurators;
    }

    public PatternConfigurator getPatternConfigurator (int index) {
            return patternConfigurators[index];

    }



}
