package de.thws.pickers;

import com.diogonunes.jcolor.Attribute;
import de.thws.exceptions.ConfiguratorException;
import de.thws.configurators.CompositionConfigurator;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.ConfiguratorHelper;
import de.thws.helpers.AppDetailsHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.diogonunes.jcolor.Ansi.colorize;

public class CompositionPicker {
    private final String soundsPath;

    public CompositionPicker (String soundsPath) {
        this.soundsPath = soundsPath;
    }


    /**
     * Lists all available compositions and let the user pick one.
     * @return path to folder the chosen composition as {@link String} or empty string if no compositions were found or an error occurred
     * @throws MenuExitException if the user entered an exit command
     */
    public String pickComposition() throws MenuExitException {
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

        System.out.println(colorize("Pick a composition to play:", Attribute.BOLD()));
        for(int i=0; i<configuratorMap.size(); i++) {
            System.out.print(colorize("[" + i + "] ", Attribute.BRIGHT_GREEN_TEXT()));
            System.out.println(configuratorMap.values().stream().toList().get(i).toString());
        }

        String picked = "";

        Scanner sc = new Scanner(System.in);
        int input = Integer.MAX_VALUE;
        boolean isInputValid = false;
        while(!isInputValid) {
            System.out.print("Enter a composition number: ");
            try {
                input = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AppDetailsHelper.checkIfExitEntered(sc);
                System.out.println(colorize("Invalid Composition Number! Please try again.", Attribute.BLACK_TEXT(), Attribute.BRIGHT_GREEN_BACK()));
                sc.nextLine();
                continue;
            }

            if(input >= configuratorMap.size() || input < 0) {
                AppDetailsHelper.displayErrorMessage("ERROR: Composition number out of bonds! Please try again.");
            }
            else {
                isInputValid = true;
                picked = configuratorMap.keySet().stream().toList().get(input).getPath();
            }
        }
        return picked;
    }

    /**
     * Searches for composition configuration files in the sounds path and returns them as a map of path as {@link File} object and {@link CompositionConfigurator} object.
     * @return {@link Map} of pairs of {@link CompositionConfigurator} objects and the path to the associated {@code config.json} files in the subfolders as {@link File} object
     * @throws ConfiguratorException if any of the {@code config.json} files cannot be read or found
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
     * Looks for {@code config.json} file in the {@code folder} and returns a {@link CompositionConfigurator} object from the JSON file.
     * If no JSON file was found in the folder returns null.
     * @param folder folder to look {@code config.json} in
     * @return {@link CompositionConfigurator} object from the {@code config.json} file or null
     * @throws ConfiguratorException if the file cannot be read or found
     */
    private CompositionConfigurator getConfigFileFromFolder(File folder) throws ConfiguratorException {
        File[] folderContent = folder.listFiles();
        if(folderContent != null) {
            for(File file : folderContent) {
                if(file.getName().equals("composition-config.json")) {
                    return ConfiguratorHelper.convertJsonFileToCompositionConfigurator(file.getPath());
                }
            }
        }
        return null;

    }

}
