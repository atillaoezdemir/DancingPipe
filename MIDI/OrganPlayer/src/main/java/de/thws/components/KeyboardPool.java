package de.thws.components;

import de.thws.configurators.KeyboardConfigurator;
import de.thws.configurators.KeyboardConfiguratorWithPath;
import de.thws.exceptions.ConfiguratorException;
import de.thws.exceptions.OrganSequencerException;
import de.thws.helpers.ConfiguratorHelper;
import de.thws.helpers.KeyboardPoolHelper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Represents all the keyboards, which are going to be used for a single composition.
 * All keyboards must have the same <a href="http://www.harfesoft.de/aixphysik/sound/midi/pages/miditmcn.html">resolution</a> (See {@link KeyboardPoolHelper}).
 * Each composition must contain at least one keyboard.
 * <p><strong>Class members:</strong>
 * <ul>
 *     <li> {@code keyboards} - {@link List} of {@link Keyboard}s used in the composition.
 *     <li> {@code beatLengthInTicks} - length of one beat in the sequence in MIDI ticks as {@code long}.
 * </ul>
 * @see Keyboard
 * @see Composition
 * @see Pattern
 */
@Getter
public class KeyboardPool {

    private final List<Keyboard> keyboards;
    private final long beatLengthInTicks;

    /**
     * Constructs a KeyboardBool object by reading the JSON configuration files in each folder in the given directory
     * @param directoryPath path to the directory which contains all the folders for the MIDI files for each of the manuals
     * @throws ConfiguratorException if directory is empty or there is no JSON configuration file in it
     */
    public KeyboardPool(File directoryPath) throws ConfiguratorException, OrganSequencerException {
        this.keyboards = new ArrayList<>();
        File[] filesInDirectory = directoryPath.listFiles();
        if(filesInDirectory == null) {
            throw new ConfiguratorException("Directory does not exist or is empty");
        }

        for(File file : filesInDirectory) {
            if(file.isDirectory()) {
                if(ConfiguratorHelper.getFirstJsonFileInDirectory(file) != null) {
                    File jsonFile = ConfiguratorHelper.getFirstJsonFileInDirectory(file);
                    KeyboardConfigurator keyboardConfigurator = ConfiguratorHelper.convertJsonFileToKeyboardConfigurator(jsonFile.getPath());
                    KeyboardConfiguratorWithPath keyboardConfiguratorWithPath =  new KeyboardConfiguratorWithPath(keyboardConfigurator, file.getPath());
                    Keyboard keyboard = new Keyboard(keyboardConfiguratorWithPath);
                    this.keyboards.add(keyboard);
                }
                else {
                    throw new ConfiguratorException("No JSON configuration file in " + file.getAbsolutePath() + "!");
                }
            }
        }

        int resolution = KeyboardPoolHelper.getKeyboardPoolResolution(this.keyboards); // resolution = number of ticks per quarter note
        this.beatLengthInTicks = resolution * 4L; // only works for time signature 4/4!, improve on future versions

    }

    public KeyboardPool(KeyboardPool keyboardPool) {
        this.keyboards = keyboardPool.getKeyboards();
        this.beatLengthInTicks = keyboardPool.getBeatLengthInTicks();
    }
}
