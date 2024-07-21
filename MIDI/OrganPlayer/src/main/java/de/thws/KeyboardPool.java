package de.thws;

import de.thws.configurators.KeyboardConfigurator;
import de.thws.configurators.KeyboardConfiguratorWithPath;
import de.thws.helpers.ConfiguratorHelper;
import de.thws.helpers.KeyboardPoolHelper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

@Getter
public class KeyboardPool {

    List<Keyboard> keyboards;
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
