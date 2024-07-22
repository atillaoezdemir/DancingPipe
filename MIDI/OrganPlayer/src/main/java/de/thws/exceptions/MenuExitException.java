package de.thws.exceptions;

/**
 * Used when the application must be terminated.
 */
public class MenuExitException extends Exception {
    public MenuExitException(String errorMessage) {
        super(errorMessage);
    }
}
