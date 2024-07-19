package de.thws;

import de.thws.configurators.KeyboardConfigurator;
import de.thws.configurators.KeyboardConfiguratorWithPath;
import de.thws.configurators.PatternConfigurator;
import de.thws.helpers.ConfiguratorHelper;
import de.thws.helpers.KeyboardPoolHelper;
import lombok.Getter;

import javax.naming.ConfigurationException;
import javax.sound.midi.MidiMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

@Getter
public class KeyboardPool {

    List<Keyboard> keyboards;
    private final long beatLengthInTicks;

    /*
    public KeyboardPool(File directoryPath) throws OrganSequencerException {

        //todo add check if directory null

        keyboards = Arrays.stream(directoryPath.listFiles())
                .map(file -> {
                    if (file.isDirectory()) {
                        return new Keyboard(file);
                    }
                    throw new RuntimeException("Not a directory: " + file);
                }).collect(Collectors.toList());

        int firstResolution = keyboards.getFirst().getResolution();
        for (int i = 0; i < keyboards.size(); i++) {
            if (keyboards.get(i).getResolution() != firstResolution) {
                throw new OrganSequencerException("Error in Keyboard " + keyboards.get(i).getKeyboardName() + "!\nAll Keyboards should have the same resolution!");
            }
        }
        this.beatLengthInTicks = firstResolution * 4L;
    }

     */

    /**
     * Constructs a KeyboardBool object by reading the JSON configuration files in each folder in the given directory
     * @param directoryPath path to the directory which contains all the folders for the MIDI files for each of the manuals
     * @throws ConfiguratorException
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
                    throw new ConfiguratorException("No JSON configuration file in " + file.getPath() + "!");
                }
            }
        }

        int resolution = KeyboardPoolHelper.getKeyboardPoolResolution(this.keyboards);
        this.beatLengthInTicks = resolution * 4L;

    }




            /*
            keyboard.getKeyboardPatterns().stream().forEach(pattern -> {
                for(int i = 0; i < pattern.getNumberOfMidiEvents(); i++) {
                    OrganEvent oldEvent = pattern.getOrganEvent(i);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (oldEvent.getTick() * this.tempoFactor);
                    pattern.setEvent(i, new OrganEvent(oldMessage, factoredTick));
                }
            });
        });

    }

             */

    // todo this should be in OrganSequencer


    public void buildFromConfiguration(Configurator configurator) {


    }

}
