package de.thws;

import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.configurators.AppConfigurator;
import de.thws.exceptions.ConfiguratorException;

import java.io.*;

import static com.diogonunes.jcolor.Ansi.colorize;
import static de.thws.helpers.ConfiguratorHelper.readFileAsStream;

/**
 * Displays the application details. This class uses the {@link AppConfigurator} class to read the application details from the file {@code app-config.json}.
 */
public class AppDetails {

    /**
     * Displays the application details.
     */
    public static void displayDetails() {
        displayLogo();
        displayAppInfo();
        System.out.println("\n");
        System.out.println("Use the menu to navigate the application.");
        System.out.print("Type ");
        System.out.print(colorize("EXIT", Attribute.BOLD(), Attribute.GREEN_TEXT()));
        System.out.println(" to exit the application.\n");
    }

    /**
     * Reads the application logo from the file {@code ascii-logo.txt} in {@code assets} folder and displays it.
     */
    private static void displayLogo() {
        File logoFIle = new File("assets/ascii-logo.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(logoFIle))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String logo = sb.toString();
            System.out.println(colorize(logo, Attribute.BRIGHT_MAGENTA_TEXT()));

        } catch (IOException _) {}
    }

    /**
     * Reads the application information from the file {@code app-config.json} and displays it using the {@link AppConfigurator} class.
     */
    private static void displayAppInfo() {
        try {
            AppConfigurator appConfigurator = readAppConfig("app-config.json");
            System.out.println(colorize(appConfigurator.getAppName(), Attribute.BOLD()));
            System.out.println(colorize("Version: " + appConfigurator.getAppVersion(), Attribute.YELLOW_BACK(), Attribute.BLACK_TEXT()));
            System.out.print("Authors: ");
            for(String author : appConfigurator.getAppAuthors()) {
                System.out.print(colorize(author + "\t", Attribute.ITALIC()));
            }
            System.out.println("\n");
            System.out.println(colorize(appConfigurator.getAppDescription(), Attribute.BRIGHT_CYAN_TEXT(), Attribute.BOLD()));
        } catch (ConfiguratorException _) {
            // just don't display the app info
        }
    }

    /**
     * Returns the content of the given file as {@link AppConfigurator} object.
     * @param path path of the file to be read
     * @return content of the file as {@link AppConfigurator} object
     * @throws ConfiguratorException if the file cannot be read
     */
    private static AppConfigurator readAppConfig(String path) throws ConfiguratorException {
        File file = new File(path);
        FileInputStream fis = readFileAsStream(file);

        String json;
        ObjectMapper mapper = new ObjectMapper();
        AppConfigurator result;
        try {
            json = new String(fis.readAllBytes());
            result = mapper.readValue(json, AppConfigurator.class);
            fis.close();
        } catch (IOException e) {
            throw new ConfiguratorException("File " + file.getName() + " could not be read");
        }

        return result;


    }
}
