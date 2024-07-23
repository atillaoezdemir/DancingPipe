package de.thws.components;

import de.thws.configurators.CompositionConfigurator;
import de.thws.exceptions.ConfiguratorException;
import de.thws.exceptions.OrganSequencerException;
import de.thws.helpers.ConfiguratorHelper;
import lombok.Getter;

import java.io.File;

/**
 * Represents a composition, which is going to be played on the organ.
 * <p>Class members:
 * <ul>
 *     <li> {@code name} - title of the composition as {@link String}.
 *     <li> {@code composer} - composer of the composition as {@link String}.
 *     <li> {@code lengthInBars} - length of the composition in bars as {@code long}.
 *     <li> {@code tempoFactor} - the tempo factor, which is used when changing the tempo of the composition as {@code float}. <i>Normally the value is between 0.1 and 0.5.</i>
 *     <li> {@code keyboardPool} - the keyboards, which are contained in the composition as {@link KeyboardPool} object.
 * </ul>
 */
@Getter
public class Composition {
    private final String name;
    private final String composer;
    private final long lengthInBars;
    private final float tempoFactor;
    private final KeyboardPool keyboardPool;


    /**
     * Constructs a {@link Composition} object from the {@code config.json}<sup>*</sup> file in the composition path using the {@link CompositionConfigurator} class.
     * <p><small><sup>*</sup> For more information about the construction of the config.json file see {@link CompositionConfigurator}</small></p>
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
     * Finds the path to the {@code config.json} file in the given directory.
     * @param path path to the parent directory as {@link File} object
     * @return path to the {@code config.json} file (if exists) as {@link String}
     * @throws ConfiguratorException if the given path does not contain a {@code config.json} file
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
