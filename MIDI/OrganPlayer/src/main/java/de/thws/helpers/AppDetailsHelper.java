package de.thws.helpers;

import com.diogonunes.jcolor.Attribute;
import de.thws.AppDetails;
import de.thws.exceptions.MenuExitException;

import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * Contains methods used in the {@link AppDetails} class
 */
public class AppDetailsHelper {
    /**
     * Checks if the user entered {@code exit} ignoring the case.
     * @param sc the {@link Scanner} to where to check
     * @throws MenuExitException if the user entered exit
     */
    public static void checkIfExitEntered(Scanner sc) throws MenuExitException {
        String strInput = sc.next();
        if(strInput.equalsIgnoreCase("EXIT")) {
            throw new MenuExitException("");
        }
    }

    /**
     * Displays a message when exiting the application.
     */
    public static void displayEndMessage() {
        System.out.println(colorize("\nThank you for playing! Bye!", Attribute.BOLD(), Attribute.CYAN_TEXT()));
    }

    /**
     * Display the message {@code msg} with red background
     * @param msg message to be displayed
     */
    public static void displayErrorMessage(String msg) {
        System.out.println(colorize(msg, Attribute.RED_BACK(), Attribute.BLACK_TEXT()));
    }
}
