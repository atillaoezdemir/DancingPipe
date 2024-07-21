package de.thws.helpers;

import com.diogonunes.jcolor.Attribute;
import de.thws.exceptions.MenuExitException;

import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class MenuHelper {
    public static void checkIfExitEntered(Scanner sc) throws MenuExitException {
        String strInput = sc.next();
        if(strInput.equalsIgnoreCase("EXIT")) {
            throw new MenuExitException("");
        }
    }

    public static void displayEndMessage() {
        System.out.println(colorize("\nThank you for playing! Bye!", Attribute.BOLD(), Attribute.CYAN_TEXT()));
    }
}
