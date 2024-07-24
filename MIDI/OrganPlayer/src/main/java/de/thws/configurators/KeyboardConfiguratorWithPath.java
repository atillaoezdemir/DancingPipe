package de.thws.configurators;

public class KeyboardConfiguratorWithPath extends KeyboardConfigurator {

    public KeyboardConfiguratorWithPath(KeyboardConfigurator keyboardConfigurator, String path) {
        this.keyboardName = keyboardConfigurator.getKeyboardName();

        for (PatternConfigurator pc : keyboardConfigurator.patternConfigurators) {
            pc.patternFile = path + "\\" + pc.patternFile;
        }
        this.patternConfigurators = keyboardConfigurator.patternConfigurators;
    }
}
