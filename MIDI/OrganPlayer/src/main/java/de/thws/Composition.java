package de.thws;

import de.thws.configurators.CompositionConfigurator;
import de.thws.helpers.ConfiguratorHelper;
import lombok.Getter;

import java.io.File;

@Getter
public class Composition {
    private final String name;
    private final String composer;
    private final long lengthInBars;
    private final float tempoFactor;
    private final KeyboardPool keyboardPool;


    /**
     * Constructs a Composition object from the config.json file in the composition path.
     * @param compositionPath path to the composition folder
     * @throws ConfiguratorException if the path does not exist or is not a directory
     * @throws OrganSequencerException
     */
    public Composition(String compositionPath) throws ConfiguratorException, OrganSequencerException {
        File pathAsFile = new File(compositionPath);
        if (!pathAsFile.exists() && !pathAsFile.isDirectory()) {
            throw new ConfiguratorException("Path " + compositionPath + " does not exist or is not a directory");
        }
        String jsonFilePath = getJsonFilePath(pathAsFile);

        CompositionConfigurator configurator = ConfiguratorHelper.convertJsonFileToCompositionConfigurator(jsonFilePath);
        this.name = configurator.getCompositionName();
        this.composer = configurator.getComposer();
        this.lengthInBars = configurator.getLengthInBars();
        this.tempoFactor = configurator.getTempoFactor();
        this.keyboardPool = new KeyboardPool(pathAsFile);
    }

    /**
     * Finds the path to the JSON Configuration file in the given directory.
     * @param path path to the parent directory
     * @return path to the JSON Configuration file (if exists) as String
     * @throws ConfiguratorException
     */
    private static String getJsonFilePath(File path) throws ConfiguratorException {
        File[] directoryContent = path.listFiles();
        if (directoryContent == null) {
            throw new ConfiguratorException("Path " + path.getAbsolutePath() + " is empty");
        }
        String jsonFilePath = "";
        for (File f : directoryContent) {
            if (f.getName().equals("config.json")) {
                jsonFilePath = f.getPath();
                break;
            }
        }
        if (jsonFilePath.isEmpty()) {
            throw new ConfiguratorException("Path " + path.getAbsolutePath() + " does not contain a JSON-Configuration file");
        }
        return jsonFilePath;
    }



}
