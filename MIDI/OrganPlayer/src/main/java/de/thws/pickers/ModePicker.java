package de.thws.pickers;

import com.diogonunes.jcolor.Attribute;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.AppDetailsHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class ModePicker {

    private static final String CLIENT_MODE_DISPLAY_NAME = "Server Client Mode";
    private static final String KEYBOARD_MODE_DISPLAY_NAME = "Keyboard Input Mode";

    /**
     * Lists the available modes to start the application in and lets the user pick one.
     * @return {@code true} if the user chose the server mode, {@code false} otherwise
     * @throws MenuExitException if the user entered an exit command
     */
    public static boolean pickMode() throws MenuExitException {
        displayInfo();
        System.out.println(colorize("Pick a mode:", Attribute.BOLD()));
        System.out.print(colorize("[" + 0 + "] ", Attribute.BRIGHT_YELLOW_TEXT()));
        System.out.println(CLIENT_MODE_DISPLAY_NAME);

        System.out.print(colorize("[" + 1 + "] ", Attribute.BRIGHT_YELLOW_TEXT()));
        System.out.println(KEYBOARD_MODE_DISPLAY_NAME);

        boolean picked = false;
        Scanner sc = new Scanner(System.in);
        int input = Integer.MAX_VALUE;
        boolean isInputValid = false;
        while(!isInputValid) {
            System.out.print("Enter a mode number: ");
            try {
                input = sc.nextInt();
            }
            catch (InputMismatchException e) {
                AppDetailsHelper.checkIfExitEntered(sc);
                System.out.println(colorize("Invalid mode Number! Please try again.", Attribute.BLACK_TEXT(), Attribute.BRIGHT_GREEN_BACK()));
                sc.nextLine();
                continue;
            }

            if(input >= 2 || input < 0) {
                AppDetailsHelper.displayErrorMessage("ERROR: Mode number out of bonds! Please try again.");
            }
            else {
                isInputValid = true;
                picked = input == 0;
            }
        }
        return picked;
    }

    /**
     * Displays information about the available modes in which the application can be started:
     */
    public static void displayInfo() {
        System.out.print(colorize("The application can be started in two modes\nThe ", Attribute.BRIGHT_YELLOW_TEXT()));
        System.out.print(colorize(CLIENT_MODE_DISPLAY_NAME, Attribute.BRIGHT_YELLOW_TEXT(), Attribute.BOLD()));
        System.out.print(colorize(" lets you connect with the server and use the application with the camera.\nThe ", Attribute.BRIGHT_YELLOW_TEXT()));
        System.out.print(colorize(KEYBOARD_MODE_DISPLAY_NAME, Attribute.BRIGHT_YELLOW_TEXT(), Attribute.BOLD()));
        System.out.println(colorize(" lets you control the sequencer with keyboard inputs. No connection with the server will be established. ", Attribute.BRIGHT_YELLOW_TEXT()));
    }
}
