package de.thws.configurators;

/**
 * A class that extends {@link KeyboardConfigurator} providing the full path to the MIDI files in the {@link PatternConfigurator}
 */
public class KeyboardConfiguratorWithPath extends KeyboardConfigurator {

    /**
     * Constructs a {@link KeyboardConfiguratorWithPath} object using a {@link KeyboardConfigurator} object and the {@code path}.
     * This path is then used to determine the full path to the MIDI files.
     * @param keyboardConfigurator base object to use in the constructor 
     * @param path path to the folder, where the configurator for the keyboard is
     */
    public KeyboardConfiguratorWithPath(KeyboardConfigurator keyboardConfigurator, String path) {
        this.keyboardName = keyboardConfigurator.getKeyboardName();

        for (PatternConfigurator pc : keyboardConfigurator.patternConfigurators) {
            pc.patternFile = path + "\\" + pc.patternFile;
        }
        this.patternConfigurators = keyboardConfigurator.patternConfigurators;
    }
}
