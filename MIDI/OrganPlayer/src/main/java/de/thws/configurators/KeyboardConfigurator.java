package de.thws.configurators;
import lombok.Getter;

import java.io.Serializable;

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
