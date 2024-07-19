package de.thws.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.Configurator;
import de.thws.ConfiguratorException;
import de.thws.KeyboardName;
import de.thws.configurators.KeyboardConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfiguratorHelper {

    /**
     * Reads the given JSON file and converts it to KeyboardConfigurator object.
     * @param filepath path to the JSON file to read
     * @return The content of the file as KeyboardConfigurator object
     * @throws ConfiguratorException if the file cannot be found or cannot be read
     */
    public static KeyboardConfigurator convertJsonFileToKeyboardConfigurator(String filepath) throws ConfiguratorException {
        File file = new File(filepath);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ConfiguratorException("File " + file.getName() + " was not found");
        }

        String json;
        ObjectMapper mapper = new ObjectMapper();
        KeyboardConfigurator result;
        try {
            json = new String(fis.readAllBytes());
            result = mapper.readValue(json, KeyboardConfigurator.class);
            fis.close();
        } catch (IOException e) {
            throw new ConfiguratorException("File " + file.getName() + " could not be read");
        }

        return result;

    }

    /**
     * Counts the number of JSON files in the given directory.
     *
     * @param directory directory to look for JSON files in
     * @return number of JSON files in the directory
     */

    public static int countJsonFilesInDirectory(File directory) {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                count++;
            }
        }
        return count;
    }

    public static File getFirstJsonFileInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                return file;
            }
        }
        return null;
    }

    /**
     * Converts the given string to KeyboardName enum.
     * @param string
     * @return manual name as enum object
     * @throws ConfiguratorException if the given name is not a valid manual name
     */
    public static KeyboardName convertStringToKeyboardName(String string) throws ConfiguratorException {
        return switch (string.toLowerCase()) {
            case "choir" -> KeyboardName.CHOIR;
            case "great" -> KeyboardName.GREAT;
            case "swell" -> KeyboardName.SWELL;
            case "solo" -> KeyboardName.SOLO;
            case "pedal" -> KeyboardName.PEDAL;
            default -> throw new ConfiguratorException(string + " is not a valid manual name!");
        };
    }

}
