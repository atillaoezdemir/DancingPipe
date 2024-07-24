package de.thws.exceptions;

/**
 * Thrown when the application must be terminated.
 */
public class MenuExitException extends Exception {
    public MenuExitException(String errorMessage) {
        super(errorMessage);
    }
}
