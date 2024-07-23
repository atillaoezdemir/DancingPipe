package de.thws.pickers;

import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.Configurator;
import de.thws.configurators.AppConfigurator;
import de.thws.configurators.ServerAddressConfigurator;
import de.thws.exceptions.ConfiguratorException;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.AppDetailsHelper;

import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * Lets the user select the server connection address.
 */
public class ServerAddressPicker {

    /**
     * Lets the user select the server connection address and returns it as {@link String}
     *
     * @return selected address as {@link String}
     */
    public static String pickServerAddress() throws MenuExitException {
        ServerAddressConfigurator sac;
        try {
            sac = loadAddressesFromFile("server-config.json");
        } catch (ConfiguratorException e) {
            AppDetailsHelper.displayErrorMessage(e.getMessage());
            return "";
        }

        for (int i = 0; i < 2; i++) {
            System.out.print(colorize("[" + i + "] ", Attribute.BLUE_TEXT()));
            System.out.println(i == 0 ? sac.getLocal() : sac.getRemote());
        }

        String picked = "";

        Scanner sc = new Scanner(System.in);
        int input = Integer.MAX_VALUE;
        boolean isInputValid = false;
        while (!isInputValid) {
            System.out.print("Enter the Address number: ");
            try {
                input = sc.nextInt();
            } catch (InputMismatchException e) {
                AppDetailsHelper.checkIfExitEntered(sc);
                System.out.println(colorize("Invalid Address Number! Please try again.", Attribute.BLACK_TEXT(), Attribute.BLUE_BACK()));
                sc.nextLine();
                continue;
            }

            if (input >= 2 || input < 0) {
                AppDetailsHelper.displayErrorMessage("ERROR: Address number out of bonds! Please try again.");
            } else {
                isInputValid = true;
                picked = input == 0 ? sac.getLocal() : sac.getRemote();
            }
        }
        return picked;
    }

    /**
     * Reads the content of the given server-config.json file in {@code filepath}.
     *
     * @param filepath - path to file to read
     * @return the server addresses as {@link ServerAddressConfigurator} object
     * @throws ConfiguratorException if the file doesn't exist or an error occurred while reading it
     */
    private static ServerAddressConfigurator loadAddressesFromFile(String filepath) throws ConfiguratorException {
        File pathAsFile = new File(filepath);
        if (!pathAsFile.exists() || !pathAsFile.getName().equals("server-config.json")) {
            throw new ConfiguratorException("Path " + filepath + " doesn't exist or is not a valid configuration file.");
        }

        try (FileInputStream fis = new FileInputStream(pathAsFile)) {
            String json = new String(fis.readAllBytes());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, ServerAddressConfigurator.class);
        } catch (IOException e) {
            throw new ConfiguratorException("Error when reading the file " + filepath + ". Make sure it the field names match the requirements.");
        }
    }
}
