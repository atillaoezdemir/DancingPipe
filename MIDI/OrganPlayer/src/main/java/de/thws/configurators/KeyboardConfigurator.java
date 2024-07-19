package de.thws.configurators;
import de.thws.ConfiguratorException;
import de.thws.helpers.ConfiguratorHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ConcurrentModificationException;

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
