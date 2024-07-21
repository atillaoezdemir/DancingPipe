package de.thws.client;

import de.thws.Composition;
import de.thws.ConfiguratorException;
import de.thws.OrganSequencerException;
import de.thws.configurators.CompositionConfigurator;
import de.thws.configurators.KeyboardConfigurator;
import de.thws.helpers.ConfiguratorHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CompositionPicker {
    private String soundsPath;

    public CompositionPicker (String soundsPath) {
        this.soundsPath = soundsPath;
    }


    /**
     * Lists all available compositions and let the user pick one.
     * @return path to the chosen composition or empty string if no compositions were found or an error occurred
     */
    public String pickComposition() {
        Map<File, CompositionConfigurator> configuratorMap = null;
        try {
            configuratorMap = getListOfCompositions();
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return "";
        }
        catch (ConfiguratorException e) {
            System.out.println(e.getMessage());
        }

        if(configuratorMap == null ||  configuratorMap.isEmpty()) {
            System.out.println("No compositions found");
            return "";
        }

        System.out.println("Pick a composition to play:");
        for(int i=0; i<configuratorMap.size(); i++) {
            System.out.println("[" + i + "] " + configuratorMap.values().stream().toList().get(i).toString());
        }

        String picked = "";

        Scanner sc = new Scanner(System.in);
        int input = Integer.MAX_VALUE;
        boolean isInputValid = false;
        while(!isInputValid) {
            System.out.println("Enter a composition number: ");
            try {
                input = sc.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid Composition Number! Please try again.");
                sc.nextLine();
                continue;
            }

            if(input >= configuratorMap.size() || input < 0) {
                System.out.println("ERROR: MIDI Device number out of bonds! Please try again.");
            }
            else {
                isInputValid = true;
                picked = configuratorMap.keySet().stream().toList().get(input).getPath();
            }
        }
        return picked;
    }

    /**
     * Searches for composition configuration files in the sounds path and returns them as a map of path and CompositionConfigurator object.
     * @return Map of pairs of CompositionConfigurator objects and the path to the associated config.json files in the subfolders
     * @throws ConfiguratorException if any of the config.json files cannot be read or found
     * @throws FileNotFoundException if the sounds path is invalid
     */
    private Map<File, CompositionConfigurator> getListOfCompositions () throws ConfiguratorException, FileNotFoundException {
        File pathAsFile = new File(soundsPath);
        if(!pathAsFile.exists() || !pathAsFile.isDirectory()) {
            throw new FileNotFoundException("Path " + this.soundsPath + " doesn't exist or is not directory");
        }
        Map<File, CompositionConfigurator> configuratorMap = new HashMap<>();
        File[] files = pathAsFile.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                CompositionConfigurator configurator = getConfigFileFromFolder(file);
                if(configurator != null) {
                    configuratorMap.put(file, configurator);
                }
            }
        }

        return configuratorMap;
    }


    /**
     * Looks for config.json file in the folder and returns a CompositionConfigurator object from the JSON file.
     * If no JSON file was found in the folder returns null.
     * @param folder folder to look config.json in
     * @return CompositionConfigurator object from the config.json file or null
     * @throws ConfiguratorException if the file cannot be read or found
     */
    private CompositionConfigurator getConfigFileFromFolder(File folder) throws ConfiguratorException {
        File[] folderContent = folder.listFiles();
        if(folderContent != null) {
            for(File file : folderContent) {
                if(file.getName().equals("config.json")) {
                    return ConfiguratorHelper.convertJsonFileToCompositionConfigurator(file.getPath());
                }
            }
        }
        return null;

    }

}
